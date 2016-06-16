package thunder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HandGunBreak on 2016/4/7 - 18:13.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: Rpc rpc接口类注解
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Rpc {

}
