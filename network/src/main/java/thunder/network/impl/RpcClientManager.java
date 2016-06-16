package thunder.network.impl;

import okhttp3.Interceptor;
import thunder.network.LogHandler;
import thunder.network.ResultGenerator;
import thunder.network.RpcRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HandGunBreak on 2016/4/21 - 20:33.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: RpcClientManager 管理类
 */
@SuppressWarnings("unused")
public class RpcClientManager {

    private static boolean DEBUG = true;
    public static LogHandler mLogHandler = new DefaultLogHandler();

    //http默认端口
    private static String mPort = "80";
    //https端口
    private static String mSecPort = "8443";
    //
    private static String mDomain = "localhost:";

    static ResultGenerator resultGenerator = new DefaultResultGenerator();

    //设置结果生成对象
    public static void setResultGenerator(ResultGenerator resultGenerator) {

        if (resultGenerator != null) {

            RpcClientManager.resultGenerator = resultGenerator;
        }
    }

    public static String getApiDomain() {

        return mDomain;
    }

    public static boolean isDebug() {

        return DEBUG;
    }

    /**
     * 是否开启调试模式
     *
     * @param debug 是否开启
     */
    public static void setDebug(boolean debug) {

        RpcClientManager.DEBUG = debug;
    }

    public static void setLogHandler(LogHandler logHandler) {

        RpcClientManager.mLogHandler = logHandler;
    }

    /**
     * 设置API的域名
     *
     * @param domain 域名
     */
    public static void setApiDomain(String domain) {

        mDomain = domain;
    }


    public static String getApiPort() {

        return mPort;
    }

    public static String getSecApiPort() {

        return mSecPort;
    }

    /**
     * 设置Https端口叼
     *
     * @param secPort 端口号
     * @return
     */
    public static void setSecApiPort(String secPort) {
        RpcClientManager.mSecPort = secPort;
    }

    /**
     * 设置API的端口号
     *
     * @param port 端口号
     */
    public static void setApiPort(String port) {
        mPort = port;
    }

    //系统级自定义拦截器
    private static List<RpcRequestInterceptor> globalRpcRequestInterceptor = new ArrayList<>();

    //系统级Application拦截器
    private static List<Interceptor> globalApplicationInterceptor = new ArrayList<>();

    //系统级Network拦截器
    private static List<Interceptor> globalNetworkInterceptor = new ArrayList<>();

    /**
     * 添加全局自定义拦截器
     *
     * @param rpcRequestInterceptor 拦截器
     */
    public static void addGlobalInterceptor(RpcRequestInterceptor rpcRequestInterceptor) {

        if (rpcRequestInterceptor != null) {

            globalRpcRequestInterceptor.add(rpcRequestInterceptor);
        }
    }

    /**
     * 添加全局自定义拦截器s
     *
     * @param rpcRequestInterceptors 拦截器s
     */
    public static void addGlobalInterceptors(List<RpcRequestInterceptor> rpcRequestInterceptors) {

        if (rpcRequestInterceptors != null && rpcRequestInterceptors.size() > 0) {

            globalRpcRequestInterceptor.addAll(rpcRequestInterceptors);
        }
    }

    /**
     * 返回全局自定义拦截器
     *
     * @return 拦截器
     */
    public static List<RpcRequestInterceptor> getGlobalInterceptor() {

        return globalRpcRequestInterceptor;
    }

    /**
     * 添加全局Application拦截器
     *
     * @param interceptor 拦截器
     */
    public static void addGlobalApplicationInterceptor(Interceptor interceptor) {

        if (interceptor != null) {

            globalApplicationInterceptor.add(interceptor);
        }
    }

    /**
     * 添加全局Application拦截器s
     *
     * @param interceptors 拦截器s
     */
    public static void addGlobalApplicationInterceptors(List<Interceptor> interceptors) {

        if (interceptors != null && interceptors.size() > 0) {

            globalApplicationInterceptor.addAll(interceptors);
        }
    }

    /**
     * 返回全局Application拦截器
     *
     * @return 拦截器s
     */
    public static List<Interceptor> getGlobalApplicationInterceptor() {

        return globalApplicationInterceptor;
    }

    /**
     * 添加全局Network拦截器
     *
     * @param interceptor 拦截器
     */
    public static void addGlobalNetworkInterceptor(Interceptor interceptor) {

        if (interceptor != null) {

            globalNetworkInterceptor.add(interceptor);
        }
    }

    /**
     * 添加全局Network拦截器s
     *
     * @param interceptors 拦截器s
     */
    public static void addGlobalNetworkInterceptors(List<Interceptor> interceptors) {

        if (interceptors != null && interceptors.size() > 0) {

            globalNetworkInterceptor.addAll(interceptors);
        }
    }

    /**
     * 返回全局Network拦截器s
     *
     * @return 拦截器s
     */
    public static List<Interceptor> getGlobalNetworkInterceptor() {

        return globalNetworkInterceptor;
    }
}
