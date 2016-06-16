package thunder.network.impl;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Gary_Cheung on 15/10/30.
 */
public class RpcResponse {

    /**
     * 状态码
     */
    public String statusCode;

    /**
     * 异常消息，仅在statusCode !=1的时候出现
     */
    public String message;

    /**
     * 业务数据响应结果(content-type为二进制时有效)
     */
    public byte[] responseBytes;

    /**
     * 业务数据响应结果
     */
    public String responseBody;

    /**
     * Bitmap结果
     */
    public Bitmap bitmap;

    /**
     * File结果
     */
    public File file;

    /**
     * 响应头部
     */
    public Map<String, List<String>> headers;

    public static class AbortRpcResponse extends RpcResponse {

        private static volatile AbortRpcResponse instance = new AbortRpcResponse();

        public static AbortRpcResponse make() {

            return instance;
        }
    }

}
