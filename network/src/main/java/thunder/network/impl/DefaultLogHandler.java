package thunder.network.impl;

import thunder.network.LogHandler;

/**
 * Created by HandGunBreak on 2016/5/5 - 18:04.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: 日志处理类
 */
public class DefaultLogHandler implements LogHandler {

    public void error(String message, Throwable throwable) {

        if (RpcClientManager.isDebug()) {

            System.out.println("==error==\n" + message + "; \n" + (throwable != null ? throwable.toString() : ""));
        }
    }

    public void warn(String message, Throwable throwable) {

        if (RpcClientManager.isDebug()) {

            System.out.println("==warn==\n" + message + "; \n" + (throwable != null ? throwable.toString() : ""));
        }
    }

    @Override
    public void info(String message, Throwable throwable) {

        if (RpcClientManager.isDebug()) {

            System.out.println(message + "; \n" + (throwable != null ? throwable.toString() : ""));
        }
    }
}
