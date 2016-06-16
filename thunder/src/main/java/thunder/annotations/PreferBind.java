package thunder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HandGunBreak on 2016/4/23 - 11:27.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface PreferBind {

}
