package thunder.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HandGunBreak on 2016/5/14 - 15:23.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: Rpc 服务类型注解
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface RpcApi {

    String value() default "";

    long readTimeout() default 20000L;

    long writeTimeout() default 20000L;

    long connectionTimeout() default 20000L;

    boolean isHttps() default false;

    int methodType() default MethodType.GET;
}
