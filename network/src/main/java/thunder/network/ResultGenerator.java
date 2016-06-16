package thunder.network;

import thunder.network.impl.RpcResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by whoislcj on 2015/11/5 - 17:39.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description:
 */
public interface ResultGenerator {

    RpcResponse createResponse(byte[] responseBodyBytes, Map<String, List<String>> header);

    RpcResponse createBitmapResponse(byte[] responseBodyBytes, Map<String, List<String>> header);

    RpcResponse createResponse(String responseBody, Map<String, List<String>> header);

    RpcResponse createFailedResponse(String statusCode, String errorMessage);
}
