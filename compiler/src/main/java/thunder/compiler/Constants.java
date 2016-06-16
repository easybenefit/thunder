package thunder.compiler;

/**
 * Created by HandGunBreak on 2016/5/15 - 11:38.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
class Constants {

    public static boolean DEBUG_MODEL = false;

    //Rpc生成类后缀
    public static final String RPC_SUFFIX = "_Rpc";

    //java类
    public static final String JAVA_PREFIX = "java.";

    //android类
    public static final String ANDROID_PREFIX = "android";

    //Bind生成类后缀
    public static final String BINDING_CLASS_SUFFIX = "_Thunder";

    //Scope注解的生成方法名
    public static final String BUILD_SCOPE_ANNOTATED_METHOD = "buildScopeAnnotatedMethod";

    //Scope注解生成的类
    public static final String SCOPE_ANNOTATED_INTERCEPTOR_CLASSNAME = "ScopeAnnotatedInterceptorClass";

}
