package thunder.compiler;

import thunder.network.util.StringUtil;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HandGunBreak on 2015/11/6 - 21:01.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description: 类级解析信息
 */
public class RpcAnnotatedInterfaceInfo implements InsertInterceptor {

    //类名
    public String className;
    //包名
    public String packageName;
    //类Element
    private TypeElement rpcServiceClassElement;
    //类级别拦截器
    protected List<String> interceptorClassName;
    //方法信息
    private List<RpcServiceMethodInfo> rpcServiceMethodInfo;
    //全局拦截器
    public ScopeAnnotatedInterceptorInfo scopeAnnotatedInterceptorInfo;

    //返回带后缀的Rpc接口类名
    public String getClassName() {

        if (className == null || className.length() == 0) {

            return null;
        }
        return className + Constants.RPC_SUFFIX;
    }

    public void setClassName(String className) {

        this.className = className;
    }

    public TypeElement getRpcServiceClassElement() {

        return rpcServiceClassElement;
    }

    public void setRpcServiceClassElement(TypeElement rpcServiceClassElement) {

        this.rpcServiceClassElement = rpcServiceClassElement;
    }

    public List<RpcServiceMethodInfo> getRpcServiceMethodInfo() {

        return rpcServiceMethodInfo;
    }

    public List<String> getInterceptorClassName() {

        return interceptorClassName;
    }

    @Override
    public void insertInterceptor(String interceptorClazzName) {

        if (!StringUtil.isEmpty(interceptorClazzName)) {

            if (interceptorClassName == null) {

                interceptorClassName = new ArrayList<>();
            }
            interceptorClassName.add(interceptorClazzName);
        }
    }

    /**
     * 添加方法级信息类
     *
     * @param rpcServiceMethodInfo 方法级信息
     */
    public void addInvokeMethodInfo(RpcServiceMethodInfo rpcServiceMethodInfo) {

        if (rpcServiceMethodInfo == null) {

            return;
        }

        if (this.rpcServiceMethodInfo == null) {

            this.rpcServiceMethodInfo = new ArrayList<>();
        }
        this.rpcServiceMethodInfo.add(rpcServiceMethodInfo);
    }

}
