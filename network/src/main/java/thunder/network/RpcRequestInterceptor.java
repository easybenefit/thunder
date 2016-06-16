package thunder.network;

import thunder.network.impl.RequestInfo;

/**
 * Created by  Gary_Cheung  on 2016/4/7 - 18:13.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: Rest api 拦截器接口类
 */
public interface RpcRequestInterceptor {

    boolean onPreExecute(RequestInfo requestInfo);

    void onPostExecute();

}
