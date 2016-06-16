package thunder.compiler;

import thunder.network.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HandGunBreak on 2015/11/6 - 21:01.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description: 全局拦截器注解info类
 */
public class ScopeAnnotatedInterceptorInfo implements InsertInterceptor {

    public String packageName;
    //类级别拦截器
    protected List<String> interceptorClassName;

    @Override
    public void insertInterceptor(String interceptorClazzName) {

        if (!StringUtil.isEmpty(interceptorClazzName)) {

            if (interceptorClassName == null) {

                interceptorClassName = new ArrayList<>();
            }
            interceptorClassName.add(interceptorClazzName);
        }
    }
}
