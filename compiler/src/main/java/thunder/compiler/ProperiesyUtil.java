package thunder.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by HandGunBreak on 2016/5/24 - 9:21.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
public class ProperiesyUtil {

    private static Properties properties = null;

    public static void load(String fileName) {

        properties = new Properties();

        InputStream inputStream = ProperiesyUtil.class.getResourceAsStream(fileName);
        try {

            properties.load(inputStream);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public static String get(String key) {

        return properties.getProperty(key);
    }
}
