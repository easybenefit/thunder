package thunder.network.util;

/**
 * Created by HandGunBreak on 2016/4/17 - 22:32.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: TextUtil工具类
 */
public class StringUtil {

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {

        return (str == null || str.length() == 0);
    }
}
