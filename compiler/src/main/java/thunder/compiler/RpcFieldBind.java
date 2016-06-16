package thunder.compiler;

import com.squareup.javapoet.TypeName;

/**
 * Created by HandGunBreak on 2016/4/12 - 18:36.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: @RpcService注解的实例对象
 */

final class RpcFieldBind {

    public final String name;
    public final TypeName type;

    RpcFieldBind(String name, TypeName type) {

        this.name = name;
        this.type = type;
    }

    public String getDescription() {

        return "field \'" + this.name + "\'";
    }

    public String getTargetTypeName() {

        return type.toString() + Constants.RPC_SUFFIX;
    }

}

