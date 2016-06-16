package thunder.network.util;

import thunder.network.impl.TypeInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by whoislcj on 2015/11/4 - 9:26.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description:
 */
public class ReqCallbackUtils {

    public static TypeInfo getCallbackGenericType(Class<?> clazz) {

        Type reflect = clazz.getGenericSuperclass();
        TypeInfo type = getGenericType(reflect);

        if (type == null) {

            type = getGenericType(clazz.getGenericInterfaces()[0]);
        }

        return type;
    }

    private static TypeInfo getGenericType(Type type) {

        if (type != null && type instanceof ParameterizedType) {

            Type[] args = ((ParameterizedType) type).getActualTypeArguments();
            if (args != null && args.length > 0) {

                return new TypeInfo(args[0]);
            }
        }

        return null;
    }

}
