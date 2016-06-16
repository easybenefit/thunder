package thunder.network.impl;


import thunder.network.RpcRequestInterceptor;
import thunder.network.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HandGunBreak on 2015/11/6 - 21:01.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description: 请求封装对象
 */
public class RequestInfo {

    //请求资源url
    public String requestUrl;
    //方法类型
    public int methodType = MethodType.GET;
    //头部字典
    public Map<String, String> heads;
    //body字典参数
    public Map<String, Object> parameters = new HashMap<>();
    //File对象
    public List<File> fileParam = null;
    //文件路径对象
    public List<String> filePathParam = null;
    //body对象参数
    public Object bodyParameter;
    //是否用https
    public boolean https = false;
    //读超时时间
    public long readTimeout = 20000;
    //写超时时间
    public long writeTimeout = 20000;
    //自定义URL
    public String optionalUrl = null;
    //连接超时时间
    public long connectionTimeout = 20000;

    //Application Interceptors
    public List<RpcRequestInterceptor> rpcRequestInterceptors = new ArrayList<>();

    public void addFileParam(String filePath) {

        if (filePathParam == null) {

            filePathParam = new ArrayList<>();
        }
        filePathParam.add(filePath);
    }

    public void addFileParam(File file) {

        if (fileParam == null) {

            fileParam = new ArrayList<>();
        }
        fileParam.add(file);
    }

    @Override
    public String toString() {

        return "RequestInfo{\n"
                + (!StringUtil.isEmpty(requestUrl) ? "UrlSuffix=" + requestUrl + "\n" : "")
                + "MethodType=" + (methodType == MethodType.GET ? "GET" : methodType == MethodType.POST ? "POST" : methodType == MethodType.PUT ? "PUT" : methodType == MethodType.DELETE ? "DELETE" : "UNKNOWN") + "\n"
                + (heads != null && heads.size() > 0 ? "Heads=" + heads + "\n" : "")
                + (parameters != null && parameters.size() > 0 ? "Params=" + parameters + "\n" : "")
                + (fileParam != null && fileParam.size() > 0 ? "FileParam=" + fileParam + "\n" : "")
                + (filePathParam != null && filePathParam.size() > 0 ? "FilePathParam=" + filePathParam + "\n" : "")
                + (bodyParameter != null ? "BodyParams=" + bodyParameter + "\n" : "")
                + (https ? "Https=true" : "")
                + (readTimeout != 20000 ? ", ReadTimeOut=" + readTimeout : "")
                + (writeTimeout != 20000 ? ", WriteTimeOut=" + writeTimeout : "")
                + (connectionTimeout != 20000 ? ", ConnectionTimeOut=" + connectionTimeout : "")
                + (!StringUtil.isEmpty(optionalUrl) ? ", OptionalUrl=" + optionalUrl + "\n" : "")
                + "Interceptors=" + getInterceptorsDesc()
                + '}';
    }

    private String getInterceptorsDesc() {

        if (rpcRequestInterceptors == null || rpcRequestInterceptors.size() == 0) {

            return "size(0)";
        } else {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("size(%d)[", rpcRequestInterceptors.size()));
            for (RpcRequestInterceptor rpcRequestInterceptor : rpcRequestInterceptors) {

                stringBuilder.append(String.format("name(%s),", rpcRequestInterceptor.getClass().getSimpleName()));
            }
            stringBuilder.append("]\n");
            return stringBuilder.toString();
        }
    }
}
