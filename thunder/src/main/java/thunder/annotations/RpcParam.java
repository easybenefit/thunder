package thunder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by  Gary_Cheung  on 2016/4/7 - 18:13.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: Rpc api 参数注解
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface RpcParam {

    String name();
}
