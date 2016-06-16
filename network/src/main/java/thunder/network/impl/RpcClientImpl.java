package thunder.network.impl;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import thunder.network.RpcClient;
import thunder.network.RpcRequestInterceptor;
import thunder.network.RpcServiceCallback;
import thunder.network.util.ReqCallbackUtils;
import thunder.network.util.ReqHelper;
import thunder.network.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by HandGunBreak on 2016/4/14 - 19:49.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
@SuppressWarnings({"unused", "unchecked"})
public class RpcClientImpl<T> implements RpcClient<T> {

    private static OkHttpClient mOkHttpClient = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    /**
     * 解绑，同时取消网络请求
     *
     * @param object target
     */
    public static void unBind(Object object) {

        if (object != null) {

            String tag = object.getClass().getCanonicalName();

            synchronized (mOkHttpClient.dispatcher().getClass()) {

                for (Call queuedCall : mOkHttpClient.dispatcher().queuedCalls()) {

                    if (tag.equals(queuedCall.request().tag())) {

                        queuedCall.cancel();
                        RpcClientManager.mLogHandler.info(String.format("QueuedCall(%s) with tag(%s) invoke cancel()", queuedCall.toString(), tag), null);
                    }
                }
                for (Call runningCall : mOkHttpClient.dispatcher().runningCalls()) {

                    if (tag.equals(runningCall.request().tag())) {

                        runningCall.cancel();
                        RpcClientManager.mLogHandler.info(String.format("RunningCall(%s) with tag(%s)  invoke cancel()", runningCall.toString(), tag), null);
                    }
                }
            }
        }
    }

    /**
     * 执行请求
     *
     * @param requestInfo req
     * @param callback    cb
     * @param object      target
     */
    @Override
    public void doRequest(final RequestInfo requestInfo, final RpcServiceCallback<T> callback, Object object) {

        String tag = object != null ? object.getClass().getCanonicalName() : null;

        if (requestInfo != null) {

            String url = buildRpcServiceUrl(requestInfo);
            if (StringUtil.isEmpty(url)) {

                RpcClientManager.mLogHandler.error("Rpc service url is empty. please check it.", null);
                return;
            }

            RpcClientManager.mLogHandler.info("RpcServiceUrl: " + url, null);
            if (requestInfo.https) {

                mOkHttpClient = SSLTrust.configureHttps(mOkHttpClient);//SSLTrust.configureClient(mOkHttpClient);
            }
            OkHttpClient.Builder builder = mOkHttpClient.newBuilder();

            //添加自定义全局拦截器
            addGlobalInterceptor(requestInfo);
            //添加系统全局拦截器
            addGlobalSystemInterceptor(builder, requestInfo);
            //设置超时时间
            builder.connectTimeout(requestInfo.connectionTimeout, TimeUnit.MILLISECONDS);
            builder.readTimeout(requestInfo.readTimeout, TimeUnit.MILLISECONDS);
            builder.writeTimeout(requestInfo.writeTimeout, TimeUnit.MILLISECONDS);
            //new OkHttpClient
            mOkHttpClient = builder.build();

            //执行自定义拦截器的Pre方法
            if (requestInfo.rpcRequestInterceptors != null) {

                for (RpcRequestInterceptor rpcRequestInterceptor : requestInfo.rpcRequestInterceptors) {

                    rpcRequestInterceptor.onPreExecute(requestInfo);
                }
            }
            //执行网络请求
            doRealRequest(mOkHttpClient, buildRequest(requestInfo, url, tag), requestInfo, callback);
        }
    }


    /**
     * 根据RequestInfo构建请求对象
     *
     * @param requestInfo req
     * @param url         资源地址
     * @return request对象
     */
    private Request buildRequest(RequestInfo requestInfo, String url, String tag) {

        Request request = null;

        if (requestInfo != null) {

            switch (requestInfo.methodType) {

                case MethodType.DELETE:

                    request = buildDeleteRequest(url, requestInfo.parameters, requestInfo.bodyParameter, requestInfo.heads, tag);
                    break;

                case MethodType.GET:

                    request = buildGetRequest(url, requestInfo.parameters, requestInfo.heads, tag);
                    break;

                case MethodType.POST:

                    if (requestInfo.fileParam != null && requestInfo.fileParam.size() > 0) {

                        //文件上传
                        request = buildFilesPostRequest(url, requestInfo.fileParam, requestInfo.parameters, requestInfo.heads, tag);
                    } else if (requestInfo.bodyParameter != null) {

                        //Object post
                        request = buildObjectPostRequest(url, requestInfo.bodyParameter, requestInfo.heads, tag);
                    } else if (requestInfo.parameters != null) {

                        //Form post
                        request = buildObjectPostRequest(url, requestInfo.parameters, requestInfo.heads, tag);
                    }
                    break;

                case MethodType.PUT:

                    if (requestInfo.bodyParameter != null) {

                        //Object put
                        request = buildPutRequest(url, requestInfo.bodyParameter, requestInfo.heads, tag);
                    } else if (requestInfo.parameters != null) {

                        //Form put
                        request = buildPutRequest(url, requestInfo.parameters, requestInfo.heads, tag);
                    }
                    break;

                default:
                    break;
            }
        }
        return request;
    }

    /**
     * 执行网络请求
     *
     * @param okHttpClient HttpClient
     * @param request      req
     * @param requestInfo  reqInfo
     * @param callback     cb
     */
    private void doRealRequest(OkHttpClient okHttpClient, final Request request, final RequestInfo requestInfo, final RpcServiceCallback<T> callback) {

        RpcClientManager.mLogHandler.info(requestInfo.toString(), null);

        //网络执行
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException ioException) {

                if (call != null) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            RpcResponse rpcResponse = RpcClientManager.resultGenerator.createFailedResponse(Status.REQUEST_FAILED, Status.REQUEST_FAILED_MSG);
                            callback.onFailed(rpcResponse.statusCode, rpcResponse.message);

                            RpcClientManager.mLogHandler.error("statusCode: " + rpcResponse.statusCode + "\n" + "msg: " + rpcResponse.message, ioException);
                        }
                    });
                }
                ioException.printStackTrace();
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {

                if (response != null) {

                    RpcResponse rpcResponse;
                    if (callback != null) {

                        final TypeInfo typeInfo = ReqCallbackUtils.getCallbackGenericType(callback.getClass());
                        final Class<?> typeInfoComponentType = typeInfo.getComponentType();

                        if (response.code() == 200) {

                            ResponseBody responseBody = response.body();
                            Map<String, List<String>> header = parserHeader(response);

                            if (typeInfoComponentType.isAssignableFrom(byte[].class)) {

                                rpcResponse = RpcClientManager.resultGenerator.createResponse(responseBody.bytes(), header);
                            } else if (typeInfoComponentType.isAssignableFrom(Bitmap.class)) {

                                rpcResponse = RpcClientManager.resultGenerator.createBitmapResponse(responseBody.bytes(), header);
                            } else {

                                String body = responseBody.string();
                                rpcResponse = RpcClientManager.resultGenerator.createResponse(body, header);
                                RpcClientManager.mLogHandler.info(body, null);
                            }
                        } else {

                            rpcResponse = RpcClientManager.resultGenerator.createFailedResponse(String.valueOf(response.code()), "network error. response code is  " + response.code());
                        }

                        final RpcResponse _rpcResponse = rpcResponse;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {

                                if (_rpcResponse != null) {

                                    if (_rpcResponse.statusCode.equals(Status.SUCCESS)) {

                                        if (typeInfoComponentType.isAssignableFrom(byte[].class)) {

                                            callback.onSuccess((T) _rpcResponse.responseBytes);
                                        } else if (typeInfoComponentType.isAssignableFrom(Bitmap.class)) {

                                            callback.onSuccess((T) _rpcResponse.bitmap);
                                        } else {

                                            Object o = ReqHelper.parseHttpResult(typeInfo, _rpcResponse.responseBody);
                                            callback.onSuccess((T) o);
                                        }
                                    } else {

                                        callback.onFailed(_rpcResponse.statusCode, _rpcResponse.message);
                                    }
                                }
                                //执行自定义拦截器的Pre方法
                                if (requestInfo.rpcRequestInterceptors != null) {

                                    for (RpcRequestInterceptor rpcRequestInterceptor : requestInfo.rpcRequestInterceptors) {

                                        rpcRequestInterceptor.onPostExecute();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 查找该文件媒体类型
     *
     * @param filePath 文件路径
     * @return 媒体类型
     */

    private String bestGuessMimeType(String filePath) {

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(filePath);
        if (contentType == null) {

            contentType = "application/octet-stream";
        }
        return contentType;
    }

    /**
     * 组装url地址
     *
     * @param requestInfo info
     * @return 资源地址
     */
    private String buildRpcServiceUrl(RequestInfo requestInfo) {

        boolean useHttps = requestInfo.https;

        String prefix = useHttps ? Constants.HTTPS_PRE : Constants.HTTP_PRE;

        //自定义Url
        if (!StringUtil.isEmpty(requestInfo.optionalUrl)) {

            //已是完整的url地址
            if ((requestInfo.optionalUrl.startsWith(Constants.HTTP_PRE) || (requestInfo.optionalUrl.startsWith(Constants.HTTPS_PRE)))) {

                return requestInfo.optionalUrl;
            }

            return String.format("%s%s", prefix, requestInfo.optionalUrl);
        }

        String requestUrl = requestInfo.requestUrl;

        if (StringUtil.isEmpty(requestUrl)) {

            return null;
        }
        //已是完整的url地址
        if ((requestUrl.startsWith(Constants.HTTP_PRE) || (requestUrl.startsWith(Constants.HTTPS_PRE)))) {

            return requestUrl;
        }

        String domain = RpcClientManager.getApiDomain();
        String port = useHttps ? RpcClientManager.getSecApiPort() : RpcClientManager.getApiPort();

        if (StringUtil.isEmpty(domain)) {

            throw new RuntimeException(" ** Client must set service domain and port.");
        }

        return String.format("%s%s%s%s", prefix, domain, (!StringUtil.isEmpty(port) ? ":" + port : ""), requestUrl);
    }

    /**
     * build Query param a=1&b=3
     *
     * @param queryParam param
     * @return res
     */
    private String buildQueryParam(Map<String, Object> queryParam) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : queryParam.keySet()) {

            Object object = queryParam.get(key);
            if (object != null) {

                stringBuilder.append(key).append("=").append(object).append("&");
            }
        }

        if (stringBuilder.length() > 0) {

            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    /**
     * 添加全局自定义拦截器
     *
     * @param requestInfo req
     */
    private void addGlobalInterceptor(RequestInfo requestInfo) {

        if (requestInfo != null) {

            if (RpcClientManager.getGlobalInterceptor() != null && RpcClientManager.getGlobalInterceptor().size() > 0) {

                requestInfo.rpcRequestInterceptors.addAll(RpcClientManager.getGlobalInterceptor());
            }
        }
    }

    /**
     * 添加全局系统定义拦截器
     *
     * @param builder     builder
     * @param requestInfo req
     */
    private void addGlobalSystemInterceptor(OkHttpClient.Builder builder, RequestInfo requestInfo) {

    }


    /**============================================请求访求（Delete, Get、Post、Put）================================================
     * ====================================================================================================================*/

    /**
     * Delete请求
     *
     * @param url        url地址
     * @param parameters map参数
     * @param body       obj参数
     * @param headers    头部
     * @param tag        标签
     * @return
     */
    private Request buildDeleteRequest(String url, Map<String, Object> parameters, Object body, Map<String, String> headers, String tag) {

        url = url + ((parameters == null || parameters.size() == 0) ? "" : "?" + buildQueryParam(parameters));

        String bodyStr = "";
        if (parameters != null && parameters.size() > 0) {

            bodyStr = JSON.toJSONString(parameters);
        } else if (body != null) {

            bodyStr = JSON.toJSONString(body);
        }

        return buildDeleteRequest(url, bodyStr, headers, tag);
    }


    /**
     * 构建Put请求
     *
     * @param url
     * @param body
     * @param header
     * @return
     */
    private Request buildDeleteRequest(String url, String body, Map<String, String> header, String tag) {

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, body);

        Request.Builder builder = new Request.Builder();

        builder.url(url).delete(requestBody);

        RpcClientManager.mLogHandler.info("params: " + body, null);

        return buildRequestBuilder(builder, header, tag);
    }

    /**
     * Get请求
     *
     * @param url        资源的地址
     * @param parameters 字典参数
     * @param header     字典头部
     * @return RestClient
     */
    private Request buildGetRequest(String url, Map<String, Object> parameters, Map<String, String> header, String tag) {

        url = url + ((parameters == null || parameters.size() == 0) ? "" : "?" + buildQueryParam(parameters));

        Request.Builder builder = new Request.Builder().url(url);

        return buildRequestBuilder(builder, header, tag);
    }

    /**
     * @param url
     * @param object
     * @param header
     * @param tag
     * @return
     */
    private Request buildObjectPostRequest(String url, Object object, Map<String, String> header, String tag) {

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(object)));

        RpcClientManager.mLogHandler.info("params: " + JSON.toJSONString(object), null);

        return buildRequestBuilder(builder, header, tag);
    }


    /**
     * Build Form表单的Post Request
     *
     * @param url        url
     * @param parameters param
     * @param header     header
     * @return req
     */
    private Request buildFormPostRequest(String url, Map<String, Object> parameters, Map<String, String> header, String tag) {

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (parameters != null && parameters.size() > 0) {

            for (String key : parameters.keySet()) {

                formBodyBuilder.add(key, String.valueOf(parameters.get(key)));
            }
        }
        Request.Builder requestBuild = new Request.Builder()
                .url(url)
                .post(formBodyBuilder.build());
        return buildRequestBuilder(requestBuild, header, tag);
    }

    /**
     * Build MultiPart Post Request.
     *
     * @param url    url
     * @param files  files
     * @param header header
     * @return req
     */
    private Request buildFilesPostRequest(String url, List<File> files, Map<String, Object> param, Map<String, String> header, String tag) {

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (param != null && param.size() > 0) {

            for (String key : param.keySet()) {

                multipartBodyBuilder.addFormDataPart(key, param.get(key).toString());
                RpcClientManager.mLogHandler.info("key:" + key + ", param:" + param.get(key).toString(), null);
            }
        }

        if (files != null && files.size() > 0) {

            for (File file : files) {

                if (file.exists()) {

                    String type = bestGuessMimeType(file.getPath());
                    multipartBodyBuilder.addFormDataPart(file.getName(), file.getName(), RequestBody.create(MediaType.parse(bestGuessMimeType(type)), file));
                    RpcClientManager.mLogHandler.info("fileName:" + file.getName() + ", filePath:" + file.getPath(), null);
                }
            }
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build());
        return buildRequestBuilder(builder, header, tag);
    }


    /**
     * Put请求
     *
     * @param url        url
     * @param parameters param
     * @param header     header
     * @return req
     */
    private Request buildPutRequest(String url, Map<String, Object> parameters, Map<String, String> header, String tag) {

        String bodyStr = null;

        if (parameters != null) {

            bodyStr = JSON.toJSONString(parameters);
        }

        return this.buildPutRequest(url, bodyStr, header, tag);
    }

    /**
     * Put请求
     *
     * @param url           url
     * @param bodyParameter body
     * @param header        header
     * @return req
     */
    private Request buildPutRequest(String url, Object bodyParameter, Map<String, String> header, String tag) {

        String body = JSON.toJSONString(bodyParameter);

        RpcClientManager.mLogHandler.info("params: " + body, null);

        return this.buildPutRequest(url, body, header, tag);
    }

    /**
     * put请求
     *
     * @param url     url
     * @param bodyStr body
     * @param header  header
     * @return req
     */
    private Request buildPutRequest(String url, String bodyStr, Map<String, String> header, String tag) {

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, bodyStr);

        Request.Builder builder = new Request.Builder();

        builder.url(url).put(requestBody);

        RpcClientManager.mLogHandler.info("params: " + bodyStr, null);

        return buildRequestBuilder(builder, header, tag);
    }

    /**
     * 构建网络请求Request对象
     *
     * @param builder 请求Builder
     * @param header  头部数据
     * @return 请求对象
     */
    private Request buildRequestBuilder(Request.Builder builder, Map<String, String> header, String tag) {

        if (header != null) {

            for (String key : header.keySet()) {

                if (header.get(key) != null) {

                    builder.addHeader(key, header.get(key));
                }
            }
        }
        if (!StringUtil.isEmpty(tag)) {

            builder.tag(tag);
        }
        return builder.build();
    }

    private ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();

    /**
     * 解析头部
     *
     * @param response
     * @return
     */
    private Map<String, List<String>> parserHeader(Response response) {

        if (response == null || response.headers() == null) {

            return null;
        }

        return response.headers().toMultimap();
    }

}
