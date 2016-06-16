package thunder.network;

import thunder.network.impl.RequestInfo;

/**
 * Created by Gary_Cheung on 15/10/29.
 */
public interface RpcClient<T> {

    void doRequest(RequestInfo requestInfo, RpcServiceCallback<T> callback, Object tag);

}
