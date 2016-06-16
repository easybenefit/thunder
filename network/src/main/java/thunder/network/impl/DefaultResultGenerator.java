package thunder.network.impl;

import android.graphics.BitmapFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import thunder.network.ResultGenerator;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by HandGunBreak on 2015/11/5 - 17:40.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description:
 */
class DefaultResultGenerator implements ResultGenerator {

    @Override
    public RpcResponse createResponse(byte[] responseBodyBytes, Map<String, List<String>> header) {

        RpcResponse response = new RpcResponse();
        response.responseBytes = responseBodyBytes;
        response.statusCode = "1";
        response.headers = header;
        return response;
    }

    @Override
    public RpcResponse createBitmapResponse(byte[] responseBodyBytes, Map<String, List<String>> header) {

        RpcResponse rpcResponse = null;

        if (responseBodyBytes != null && responseBodyBytes.length > 0) {

            rpcResponse = new RpcResponse();
            try {

                rpcResponse.bitmap = BitmapFactory.decodeByteArray(responseBodyBytes, 0, responseBodyBytes.length);
                rpcResponse.statusCode = Status.SUCCESS;
                rpcResponse.message = Status.SUCCESS_MSG;
            } catch (Exception e) {

                rpcResponse.bitmap = null;
                rpcResponse.statusCode = Status.BITMAP_DECODE_FAILED;
                rpcResponse.message = Status.BITMAP_DECODE_FAILED_MSG;
                e.printStackTrace();
            }
          /*  try {

                inputStream.close();
            } catch (Exception exception) {

                exception.printStackTrace();
            }*/
        }

        return rpcResponse;
    }

    public RpcResponse createResponse(String responseBody, Map<String, List<String>> header) {

        RpcResponse response = new RpcResponse();

        JSONObject result = JSON.parseObject(responseBody);

        if (result != null) {

            response.statusCode = result.getString("result");
            if ("1".equals(response.statusCode)) {

                response.responseBody = result.getString("data");

                response.message = result.getString("message");
            } else {

                response.message = result.getString("message");
            }
            response.headers = header;
        } else {

            response.statusCode = "-7003";

            response.message = "no response";
        }

        return response;
    }

    public RpcResponse createFailedResponse(String statusCode, String errorMessage) {

        RpcResponse response = new RpcResponse();
        response.statusCode = statusCode;
        response.message = errorMessage;

        return response;
    }
}
