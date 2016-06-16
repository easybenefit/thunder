package thunder.network.util;

import java.util.Map;

/**
 * Created by HandGunBreak on 2016/4/14 - 17:48.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
public class ObjectConvertUtil {

    /**
     * map转String
     *
     * @param map
     * @return
     */
    public static String map2String(Map<String, Object> map) {

        if (map == null || map.size() == 0) {

            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {

            Object _value = map.get(key);
            if (_value != null) {

                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(_value);
                stringBuilder.append("&");
            }
        }

        if (stringBuilder.length() > 0) {

            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }
}
