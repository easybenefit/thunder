package thunder.network;

/**
 * Created by Gary_Cheung on 15/10/30.
 * <p/>
 * Rest API调用的异步回调接口
 */
public interface RpcServiceCallback<T> {


    /**
     * 当业务调用成功后的回调
     *
     * @param result 业务结果对象
     */
    void onSuccess(T result);

    /**
     * 当业务调用异常时的回调（如：业务异常、网络异常）
     *
     * @param statusCode
     * @param errorMessage
     */
    void onFailed(String statusCode, String errorMessage);

}
