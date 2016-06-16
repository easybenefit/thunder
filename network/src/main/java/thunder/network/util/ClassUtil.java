package thunder.network.util;

import java.util.*;

@SuppressWarnings("all")
public class ClassUtil {

    private static final Map primitiveWrapperTypeMap = new HashMap(8);

    private static final Map primitiveTypeNameMap = new HashMap(16);

    static {

        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        Set primitiveTypeNames = new HashSet(16);
        primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
        primitiveTypeNames.addAll(Arrays.asList(new Class[]{boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class}));
        for (Iterator it = primitiveTypeNames.iterator(); it.hasNext(); ) {

            Class primitiveClass = (Class) it.next();
            primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
        }
    }

    public static boolean isAssignable(Class lhsType, Class rhsType) {

        return (lhsType.isAssignableFrom(rhsType) || lhsType.equals(primitiveWrapperTypeMap.get(rhsType)));
    }

    public static boolean isPrimitiveOrWrapper(Class clazz) {

        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    public static boolean isPrimitiveWrapper(Class clazz) {

        return primitiveWrapperTypeMap.containsKey(clazz);
    }
}
