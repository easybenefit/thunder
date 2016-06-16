package thunder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by HandGunBreak on 2016/4/15 - 10:09.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
@SuppressWarnings({"unused", "unchecked"})
public class Thunder {

    //Bind生成类后缀
    private static final String BINDING_CLASS_SUFFIX = "_Thunder";

    private static final RpcBind<Object> NOP_THUNDER_BINDER = new RpcBind<Object>() {

        public void bind(Object var1) {

        }

        public void unbind(Object var1) {

        }
    };

    private static boolean debug = false;

    public static void setDebug(boolean debug) {

        Thunder.debug = debug;
    }

    static final Map<Class<?>, RpcBind<Object>> BINDERS = new LinkedHashMap();

    /**
     * 查询绑定对象，即查找由Thunder.bind(this)所生气的新对象类
     * @param clazz 类名
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static RpcBind<Object> findThunderBinderForClass(Class<?> clazz) throws IllegalAccessException, InstantiationException {

        RpcBind thunderBinder = BINDERS.get(clazz);
        if (thunderBinder != null) {

            if (debug) {

                System.out.println("ThunderBinder: " + "HIT: Cached in view binder map.");
            }

            return thunderBinder;
        } else {

            String clazzName = clazz.getName();
            if (!clazzName.startsWith("android.") && !clazzName.startsWith("java.")) {

                try {

                    Class bindingClazz = Class.forName(clazzName + BINDING_CLASS_SUFFIX);
                    thunderBinder = (RpcBind) bindingClazz.newInstance();
                    if (debug) {

                        System.out.println("ThunderBinder: " + "HIT: Loaded view binder class.");
                    }
                } catch (ClassNotFoundException var4) {

                    if (debug) {

                        System.out.println("ThunderBinder: " + "Not found. Trying superclass " + clazz.getSuperclass().getName());
                    }

                    thunderBinder = findThunderBinderForClass(clazz.getSuperclass());
                }

                BINDERS.put(clazz, thunderBinder);
                return thunderBinder;
            } else {

                if (debug) {

                    System.out.println("ThunderBinder: " + "MISS: Reached framework class. Abandoning search.");
                }

                return NOP_THUNDER_BINDER;
            }
        }
    }

    /**
     * bind target object
     *
     * @param object object
     */
    public static void bind(Object object) {

        try {

            Class clazz = object.getClass();
            RpcBind<Object> binder = findThunderBinderForClass(clazz);
            binder.bind(object);
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    /**
     * unbind target object
     *
     * @param object object
     */
    public static void unbind(Object object) {

        try {

            Class clazz = object.getClass();
            RpcBind<Object> binder = findThunderBinderForClass(clazz);
            binder.unbind(object);
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

}
