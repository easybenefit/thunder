package thunder.network;

/**
 * Created by HandGunBreak on 2015/11/9 - 22:04.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description: LogHandler接口类
 */
public interface LogHandler {

    void error(String message, Throwable throwable);

    void warn(String message, Throwable throwable);

    void info(String message, Throwable throwable);
}
