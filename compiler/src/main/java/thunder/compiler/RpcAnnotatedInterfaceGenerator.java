package thunder.compiler;

import com.squareup.javapoet.*;
import okhttp3.Interceptor;

import thunder.annotations.RpcBody;
import thunder.annotations.RpcMultiPart;
import thunder.annotations.RpcParam;
import thunder.annotations.RpcUrl;
import thunder.network.impl.RequestInfo;
import thunder.network.RpcRequestInterceptor;
import thunder.network.impl.RpcClientImpl;
import thunder.network.util.MessageUtil;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HandGunBreak on 2016/4/13 - 14:22.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
public class RpcAnnotatedInterfaceGenerator {

    private static final String BuildClassInterceptorMethodName = "buildClassInterceptor";
    private static final String BuildRequestInfoMethodName = "buildRequestInfoMethodName";

    public static void brewJava(RpcAnnotatedInterfaceInfo rpcAnnotatedInterfaceInfo, Filer filer) {

        if (rpcAnnotatedInterfaceInfo == null || rpcAnnotatedInterfaceInfo.getRpcServiceMethodInfo() == null || rpcAnnotatedInterfaceInfo.getRpcServiceMethodInfo().size() <= 0) {

            return;
        }

        //方法集
        List<MethodSpec> clazzMethodSpecs = new ArrayList<>();

        //定义类级自定义拦截器方法描述
        MethodSpec clazzInterceptorMethodSpec = buildClassInterceptor(rpcAnnotatedInterfaceInfo);

        //循环生成接口定义的方法定义
        for (RpcServiceMethodInfo rpcServiceMethodInfo : rpcAnnotatedInterfaceInfo.getRpcServiceMethodInfo()) {

            //生成方法、方法修饰符、返回值
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(rpcServiceMethodInfo.mMethodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .returns(void.class);

            //每个接口定义的方法自动生成网络请求参数生成器方法定义
            MethodSpec requestMethodSpec = buildRequestMethodSpec(rpcAnnotatedInterfaceInfo, rpcServiceMethodInfo, clazzInterceptorMethodSpec);

            //添加生成网络请求参数
            methodSpecBuilder.addStatement("$T requestInfo = $N()", ClassName.get(RequestInfo.class), requestMethodSpec);
            ClassName hashMapClassName = ClassName.get(HashMap.class);
            TypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Object.class));
            methodSpecBuilder.addStatement("$T _map_param = new $T<>()", mapTypeName, hashMapClassName);

            TypeName callbackTypeName = null;
            String callbackVariableName = "null";
            String genericClassName = null;
            for (VariableElement variableElement : rpcServiceMethodInfo.getVariableElements()) {

                String variableElementName = variableElement.toString();
                methodSpecBuilder.addParameter(TypeName.get(variableElement.asType()), variableElementName, Modifier.FINAL);

                Class<? extends Annotation> clazz = checkParamAnnotationClazz(variableElement);

                if (clazz == RpcBody.class) {

                    methodSpecBuilder.addStatement("requestInfo.bodyParameter = " + variableElementName);
                    continue;
                }
                if (clazz == RpcParam.class) {

                    RpcParam rpcParam = variableElement.getAnnotation(RpcParam.class);
                    methodSpecBuilder.addStatement("_map_param.put($S, " + variableElementName + ")", rpcParam.name());
                    continue;
                }
                if (clazz == RpcUrl.class) {

                    methodSpecBuilder.addStatement("requestInfo.optionalUrl = " + variableElementName);
                    continue;
                }
                if (clazz == RpcMultiPart.class) {

                    methodSpecBuilder.addStatement("requestInfo.addFileParam(" + variableElementName + ")");
                    continue;
                } else {

                    //Callback类型
                    callbackVariableName = variableElementName;
                    genericClassName = getParamClassGenericType(variableElement);
                   /* try {

                        Class genericClazz = Class.forName(genericClassName);
                        callbackTypeName = TypeName.get(genericClazz);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }*/
                }
            }

            methodSpecBuilder.addStatement("requestInfo.parameters = _map_param");
            if (genericClassName == null) {

                methodSpecBuilder.addStatement("$T rpcClientImpl = new $T()", TypeName.get(RpcClientImpl.class), TypeName.get(RpcClientImpl.class));
            } else {

                //methodSpecBuilder.addCode(genericClassName + " _var = new " + genericClassName + "();\n");
                methodSpecBuilder.addStatement("$T _rpcClientImpl", TypeName.get(RpcClientImpl.class));
                methodSpecBuilder.addCode("RpcClientImpl<" + genericClassName + "> rpcClientImpl = new RpcClientImpl<>();\n");
            }
            methodSpecBuilder.addStatement("rpcClientImpl.doRequest(requestInfo, " + callbackVariableName + ", object)");
            clazzMethodSpecs.add(requestMethodSpec);

            //添加接口类定义方法
            clazzMethodSpecs.add(methodSpecBuilder.build());
        }

        //在自动自成类中添加类级拦截器方法描述
        if (clazzInterceptorMethodSpec != null) {

            clazzMethodSpecs.add(clazzInterceptorMethodSpec);
        }

        ClassName fieldBindClassName = ClassName.get(rpcAnnotatedInterfaceInfo.packageName, rpcAnnotatedInterfaceInfo.className);

        //添加构造方法
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "object")
                .addStatement("this.$N = $N", "object", "object")
                .build();

        //生成类
        TypeSpec classTypeSpec = TypeSpec.classBuilder(rpcAnnotatedInterfaceInfo.getClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(clazzMethodSpecs)

                .addMethod(constructor)
                .addField(Object.class, "object", Modifier.PRIVATE, Modifier.FINAL)
                .addSuperinterface(fieldBindClassName)
                .build();

        JavaFile javaFile = JavaFile.builder(rpcAnnotatedInterfaceInfo.packageName, classTypeSpec).build();

        try {

            javaFile.writeTo(filer);
        } catch (IOException ioException) {

            ioException.printStackTrace();
        }
    }

    /**
     * 构建类级拦截器方法描述
     *
     * @param rpcAnnotatedInterfaceInfo rpc
     * @return MethodSpec
     */
    private static MethodSpec buildClassInterceptor(RpcAnnotatedInterfaceInfo rpcAnnotatedInterfaceInfo) {

        if (rpcAnnotatedInterfaceInfo == null || rpcAnnotatedInterfaceInfo.getInterceptorClassName() == null || rpcAnnotatedInterfaceInfo.getInterceptorClassName().size() <= 0) {

            return null;
        }

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(BuildClassInterceptorMethodName);

        //参数类型
        ClassName interceptorClassName = ClassName.get(RpcRequestInterceptor.class);
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName returnTypeName = ParameterizedTypeName.get(list, interceptorClassName);

        //方法修饰
        methodSpecBuilder.addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        //返回类型
        methodSpecBuilder.returns(returnTypeName);
        methodSpecBuilder.addStatement("$T result = new $T<>()", returnTypeName, arrayList);

        if (rpcAnnotatedInterfaceInfo.getInterceptorClassName() != null) {

            int size = rpcAnnotatedInterfaceInfo.getInterceptorClassName().size();

            for (int i = 0; i < size; i++) {

                methodSpecBuilder.addCode("result.add((RpcRequestInterceptor)new " + rpcAnnotatedInterfaceInfo.getInterceptorClassName().get(i) + "());\n");
            }
        }
        methodSpecBuilder.addStatement("return result");
        //判断是否存在自定义的类级别拦截器
        return methodSpecBuilder.build();
    }

    /**
     * 检查注解类类型
     *
     * @param variableElement
     * @return
     */
    private static Class<? extends Annotation> checkParamAnnotationClazz(VariableElement variableElement) {

        return (variableElement.getAnnotation(RpcParam.class) != null ? RpcParam.class :
                variableElement.getAnnotation(RpcBody.class) != null ? RpcBody.class :
                        variableElement.getAnnotation(RpcUrl.class) != null ? RpcUrl.class :
                                variableElement.getAnnotation(RpcMultiPart.class) != null ? RpcMultiPart.class :
                                        null);
    }

    /**
     * 获取参数类型
     *
     * @param variableElement VariableElement
     * @return 类型字符串
     */
    private static String getParamClassType(VariableElement variableElement) {

        if (variableElement != null) {

            String paramFullName = variableElement.asType().toString();

            if (paramFullName != null && paramFullName.indexOf('<') != -1) {

                return paramFullName.substring(0, paramFullName.indexOf('<'));
            }
            return paramFullName;
        }
        return null;
    }

    /**
     * 获取参数类泛类型
     *
     * @param variableElement VariableElement
     * @return 类泛类型字符串
     */
    private static String getParamClassGenericType(VariableElement variableElement) {

        if (variableElement != null) {

            String paramFullName = variableElement.asType().toString();

            if (paramFullName != null && paramFullName.indexOf('<') != -1 && paramFullName.lastIndexOf('>') != -1) {

                if (paramFullName.lastIndexOf('>') > paramFullName.indexOf('<')) {

                    return paramFullName.substring(paramFullName.indexOf('<') + 1, paramFullName.lastIndexOf('>'));
                }
            }
            return paramFullName;
        }
        return null;
    }

    private static int index = 0;

    /**
     * 生成方法描述对象(根据 RpcServiceClassInfo对象及RpcServiceMethodInfo对象生成方法请求参数类RequestInfo)
     *
     * @param rpcAnnotatedInterfaceInfoParam rpc
     * @param rpcServiceMethodInfoParam      rpc
     * @param clazzInterceptorMethodSpec     自定义类级拦截器方法描述
     * @return 返回方法描述对象
     */
    private static MethodSpec buildRequestMethodSpec(RpcAnnotatedInterfaceInfo rpcAnnotatedInterfaceInfoParam,
                                                     RpcServiceMethodInfo rpcServiceMethodInfoParam,
                                                     MethodSpec clazzInterceptorMethodSpec) {

        ClassName requestInfoClassName = ClassName.get(RequestInfo.class);

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(String.format("%s%s%s%s%d", BuildRequestInfoMethodName, "$$", rpcServiceMethodInfoParam.mMethodName, "_", (index++)));
        //方法修饰符
        methodSpecBuilder.addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        //设置方法返回类型
        methodSpecBuilder.returns(requestInfoClassName);
        //生成返回参数类
        methodSpecBuilder.addStatement("$T requestInfo = new $T()", requestInfoClassName, requestInfoClassName);
        //请求URL
        methodSpecBuilder.addStatement("requestInfo.requestUrl = $S", rpcServiceMethodInfoParam.url);
        //请求方法
        methodSpecBuilder.addStatement("requestInfo.methodType = " + rpcServiceMethodInfoParam.methodType);
        //是否使用https
        methodSpecBuilder.addStatement("requestInfo.https = " + rpcServiceMethodInfoParam.useHttps);
        //连接超时时间
        methodSpecBuilder.addStatement("requestInfo.connectionTimeout = " + rpcServiceMethodInfoParam.connectionTimeout);
        //读超时时间
        methodSpecBuilder.addStatement("requestInfo.readTimeout = " + rpcServiceMethodInfoParam.readTimeout);
        //写超时时间
        methodSpecBuilder.addStatement("requestInfo.writeTimeout = " + rpcServiceMethodInfoParam.writeTimeout);
/*        //打印
        methodSpecBuilder.addStatement("$T.out.println(requestInfo.toString())", System.class);*/

        ClassName interceptorClassName = ClassName.get(RpcRequestInterceptor.class);
        ClassName listClassName = ClassName.get("java.util", "List");
        ClassName arrayListClassName = ClassName.get("java.util", "ArrayList");
        TypeName genericListTypeName = ParameterizedTypeName.get(listClassName, interceptorClassName);
        //methodSpecBuilder.addStatement("$T interceptors = new $T<>()", genericListTypeName, arrayListClassName);
        //methodSpecBuilder.addStatement("$T interceptors = null", genericListTypeName);//, arrayListClassName

        boolean hasCreated = false;
        //全局拦截器
        if (rpcAnnotatedInterfaceInfoParam.scopeAnnotatedInterceptorInfo != null && rpcAnnotatedInterfaceInfoParam.scopeAnnotatedInterceptorInfo.interceptorClassName != null) {

            int size = rpcAnnotatedInterfaceInfoParam.scopeAnnotatedInterceptorInfo.interceptorClassName.size();
            if (size > 0) {

                hasCreated = true;
                methodSpecBuilder.addStatement("$T interceptors = new $T<>()", genericListTypeName, arrayListClassName);
            }
            for (int interceptorIndex = 0; interceptorIndex < size; interceptorIndex++) {

                String clazzName = String.format("%s", rpcAnnotatedInterfaceInfoParam.scopeAnnotatedInterceptorInfo.interceptorClassName.get(interceptorIndex));
                methodSpecBuilder.addCode("interceptors.add((RpcRequestInterceptor)new " + clazzName + "());\n");
            }
        }

        //自定义类级拦截器
        if (clazzInterceptorMethodSpec != null) {

            if (!hasCreated) {

                hasCreated = true;
                methodSpecBuilder.addStatement("$T interceptors = new $T<>()", genericListTypeName, arrayListClassName);
            }
            methodSpecBuilder.addStatement("interceptors.addAll($N())", clazzInterceptorMethodSpec);
        }

        //自定义方法级拦截器
        if (rpcServiceMethodInfoParam.getInterceptorClassName() != null && rpcServiceMethodInfoParam.getInterceptorClassName().size() > 0) {

            if (!hasCreated) {

                hasCreated = true;
                methodSpecBuilder.addStatement("$T interceptors = new $T<>()", genericListTypeName, arrayListClassName);
            }
            for (int interceptorIndex = 0; interceptorIndex < rpcServiceMethodInfoParam.getInterceptorClassName().size(); interceptorIndex++) {

                String clazzName = String.format("%s", rpcServiceMethodInfoParam.getInterceptorClassName().get(interceptorIndex));
                methodSpecBuilder.addCode("interceptors.add((RpcRequestInterceptor)new " + clazzName + "());\n");
            }
        }
        if (hasCreated) {

            methodSpecBuilder.addStatement("requestInfo.rpcRequestInterceptors.addAll(interceptors)");
        }
        methodSpecBuilder.addStatement("return requestInfo");
        return methodSpecBuilder.build();
    }

}
