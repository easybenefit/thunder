package thunder.compiler;

import thunder.network.util.StringUtil;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by whoislcj on 2015/11/6 - 21:01.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description:
 */
@SuppressWarnings("unused")
public class RpcServiceMethodInfo implements InsertInterceptor {

    //请求资源url
    public String url;
    //方法类型
    public int methodType = MethodType.GET;
    //字典参数
    public Map<String, Object> parameters;
    //body对象参数
    public Object bodyParameters;
    //是否用https
    public boolean useHttps = false;
    //连接超时时间
    public long connectionTimeout = 2000;
    //读超时时间
    public long readTimeout = 2000;
    //写超时时间
    public long writeTimeout = 2000;
    //类级别拦截器
    protected List<String> interceptorClassName;

    public Map<String, String> mCallbackParam;

    public Map<String, String> mUrlParam;

    public Map<String, String> multiPartParams;

    //参数列表
    private List<VariableElement> variableElements;
    //回调方法参数类 java.lang.String
    public String mCallbackParamClass;
    //回调参数类com.RpcServiceCallback<java.lang.String>
    public String mCallbackClass;
    //回调参数名
    public String mCallbackParamName;
    //方法名
    public String mMethodName;

    public void addMultiPartParam(String paramName, String packageName) {

        if (paramName != null && paramName.length() > 0 && packageName != null && packageName.length() > 0) {

            if (multiPartParams == null) {

                multiPartParams = new HashMap<>();
            }
            multiPartParams.put(paramName, packageName);
        }
    }

    public void addVariableElement(VariableElement variableElement) {

        if (variableElement == null) {

            return;
        }
        if (variableElements == null) {

            variableElements = new ArrayList<>();
        }
        variableElements.add(variableElement);
    }

    public void addAllVariableElement(List<? extends VariableElement> variableElement) {

        if (variableElement == null) {

            return;
        }
        if (variableElements == null) {

            variableElements = new ArrayList<>();
        }
        variableElements.addAll(variableElement);
    }

    public List<VariableElement> getVariableElements() {

        return variableElements;
    }

    public void addUrlParam(String paramName, String packageName) {

        if (mUrlParam == null) {

            mUrlParam = new HashMap<>();
        }
        mUrlParam.put(paramName, packageName);
    }

    /**
     * 设置资源URL地址
     *
     * @param url
     * @return
     */
    public RpcServiceMethodInfo setUrl(String url) {

        this.url = url;
        return this;
    }

    /**
     * 设置访问类型
     *
     * @param methodType
     * @return
     */
    public RpcServiceMethodInfo setMethodType(int methodType) {

        this.methodType = methodType;
        return this;
    }

    /**
     * 设置body字典参数
     *
     * @param parameters
     * @return
     */
    public RpcServiceMethodInfo setParameters(Map<String, Object> parameters) {

        this.parameters = parameters;
        return this;
    }

    /**
     * 设置body对象参数
     *
     * @param bodyParameters
     * @return
     */
    public RpcServiceMethodInfo setBodyParameters(Object bodyParameters) {

        this.bodyParameters = bodyParameters;
        return this;
    }

    /**
     * 是否是Https协议
     *
     * @param useHttps
     * @return
     */
    public RpcServiceMethodInfo setUseHttps(boolean useHttps) {

        this.useHttps = useHttps;
        return this;
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

    public List<String> getInterceptorClassName() {

        return interceptorClassName;
    }
}
