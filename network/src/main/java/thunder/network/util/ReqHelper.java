package thunder.network.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import thunder.network.impl.*;
import thunder.network.impl.Void;
import thunder.network.util.ClassUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 将JSON字符串转换成指定的用户返回值类型
 *
 * @param type         TypInfo
 * @param responseData 待解析数据
 * @return
 * @throws JSONException
 */
public class ReqHelper {


    @SuppressWarnings("unchecked")
    public static <T> T parseHttpResult(TypeInfo type, String responseData) throws JSONException {

        if (thunder.network.impl.Void.class.isAssignableFrom(type.getComponentType())) { // 处理Void类型的返回值

            return (T) Void.instance();
        }
        Class<?> componentType = type.getComponentType();
        if (componentType.isAssignableFrom(String.class)) {
            return (T) responseData;
        }

        Object resultObject = JSON.parse(responseData);

        if (resultObject == null) {
            return null;
        }

        T result = null;

        Class<?> rawType = type.getRawType();

        boolean isArray = rawType != null && Array.class.isAssignableFrom(rawType);
        boolean isCollection = rawType != null && Collection.class.isAssignableFrom(rawType);

        if (rawType != null && (isArray || isCollection) && resultObject instanceof JSONArray) {

            result = convertToList(resultObject, isArray, componentType);

        } else if (rawType != null && Map.class.isAssignableFrom(rawType)) {

            result = (T) JSONObject.toJavaObject((JSONObject) resultObject, rawType);

        } else {
            // 接口的返回类型如果是简单类型，则会封装成为一个json对象，真正的对象存储在value属性上
            if (ClassUtil.isPrimitiveWrapper(componentType)) {
                result = (T) resultObject;
            } else {

                result = (T) JSONObject.toJavaObject((JSONObject) resultObject, componentType);
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> T convertToList(Object resultObject, boolean isArray, Class componentType) {
        T result;
        JSONArray jsonArray = (JSONArray) resultObject;
        int size = jsonArray.size();
        if (isArray) {

            result = (T) Array.newInstance(componentType, size);
        } else {

            result = (T) new ArrayList(size);
        }

        for (int i = 0; i < size; i++) {

            JSONObject value = jsonArray.getJSONObject(i);

            if (isArray) {

                Array.set(result, i, JSON.parseObject(value.toJSONString(), componentType));
            } else {

                ((List) result).add(JSON.parseObject(value.toJSONString(), componentType));
            }
        }

        return result;
    }
}
