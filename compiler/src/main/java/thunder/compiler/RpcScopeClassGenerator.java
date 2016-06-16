package thunder.compiler;

import com.squareup.javapoet.*;
import thunder.network.RpcRequestInterceptor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HandGunBreak on 2016/4/13 - 14:22.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: 被@RpcScope注解的元素所在类的新生成类的自动生成类
 */
public class RpcScopeClassGenerator {

    /**
     * 生成 @RpcService注解的Field所在类的新类($$ThunderBinder)
     *
     * @param scopeAnnotatedInterceptorInfo
     * @param filer
     */
    public static void brewJava(ScopeAnnotatedInterceptorInfo scopeAnnotatedInterceptorInfo, Filer filer) {

        if (scopeAnnotatedInterceptorInfo != null && scopeAnnotatedInterceptorInfo.interceptorClassName != null && scopeAnnotatedInterceptorInfo.interceptorClassName.size() > 0) {

            //参数类型
            ClassName rpcRequestInterceptor = ClassName.get(RpcRequestInterceptor.class);
            ClassName list = ClassName.get("java.util", "List");
            TypeName returnTypeName = ParameterizedTypeName.get(list, rpcRequestInterceptor);

            //生成方法、方法修饰符、返回值
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(Constants.BUILD_SCOPE_ANNOTATED_METHOD)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                    .returns(returnTypeName);

            methodSpecBuilder.addStatement("$T interceptors = new $T<>()", returnTypeName, TypeName.get(ArrayList.class));
            for (String interceptorClassName : scopeAnnotatedInterceptorInfo.interceptorClassName) {

                methodSpecBuilder.addStatement("interceptors.add(new " + interceptorClassName + "())");
            }
            methodSpecBuilder.addStatement("return interceptors");
            //生成类
            TypeSpec classTypeSpec = TypeSpec.classBuilder(Constants.SCOPE_ANNOTATED_INTERCEPTOR_CLASSNAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc("@{ was auto generated.}\n", Constants.SCOPE_ANNOTATED_INTERCEPTOR_CLASSNAME)

                    .addMethod(methodSpecBuilder.build())
                    .build();

            try {

                JavaFile javaFile = JavaFile.builder(scopeAnnotatedInterceptorInfo.packageName, classTypeSpec).build();
                javaFile.writeTo(System.out);
                javaFile.writeTo(filer);
            } catch (IOException ioException) {

                ioException.printStackTrace();
            }
        }
    }
}
