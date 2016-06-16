package thunder.compiler;

import com.squareup.javapoet.*;
import thunder.RpcBind;
import thunder.network.impl.RpcClientImpl;
import thunder.network.util.MessageUtil;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HandGunBreak on 2016/4/13 - 14:22.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: 被@RpcService注解的元素所在类的新生成类的自动生成类
 */
public class RpcBindFieldsClassGenerator {

    /**
     * 生成 @RpcService注解的Field所在类的新类($$ThunderBinder)
     *
     * @param rpcAnnotatedFieldInfoMap
     * @param filer
     */
    public static void brewJava(Map<TypeElement, RpcAnnotatedFieldInfo> rpcAnnotatedFieldInfoMap, Filer filer) {

        for (RpcAnnotatedFieldInfo rpcAnnotatedFieldInfo : rpcAnnotatedFieldInfoMap.values()) {

            List<MethodSpec> methodSpecs = new ArrayList<>();
            //生成方法、方法修饰符、返回值
            methodSpecs.add(createBindMethod(rpcAnnotatedFieldInfo, filer, rpcAnnotatedFieldInfo.targetClassName));
            methodSpecs.add(createUnbindMethod(rpcAnnotatedFieldInfo, filer, rpcAnnotatedFieldInfo.targetClassName));

            //生成类
            TypeSpec classTypeSpec = TypeSpec.classBuilder(rpcAnnotatedFieldInfo.targetClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc("@{ was auto generated.}\n", rpcAnnotatedFieldInfo.targetClassName)
                    .addMethods(methodSpecs)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(RpcBind.class), TypeVariableName.get("T")))
                    .addTypeVariable(TypeVariableName.get("T", ClassName.bestGuess(rpcAnnotatedFieldInfo.className)))
                    .build();

            try {

                JavaFile javaFile = JavaFile.builder(rpcAnnotatedFieldInfo.packageName, classTypeSpec).build();
                if (Constants.DEBUG_MODEL) {

                    javaFile.writeTo(System.out);
                    MessageUtil.warning(null, javaFile.toString());
                }
                javaFile.writeTo(filer);
            } catch (IOException ioException) {

                ioException.printStackTrace();
                /*System.out.println(ioException.toString());*/
                MessageUtil.warning(null, ioException.toString());
            }
        }
    }

    /**
     * 生成Bind方法
     *
     * @param rpcAnnotatedFieldInfo RpcField
     * @param filer                 Filer
     * @return bind方法描述
     */
    private static MethodSpec createBindMethod(RpcAnnotatedFieldInfo rpcAnnotatedFieldInfo, Filer filer, String className) {

        MethodSpec.Builder result = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("\n@bind method was auto generated in .\n", className)
                .addParameter(TypeVariableName.get("T"), "object", Modifier.FINAL);

        for (RpcFieldBind fieldBind : rpcAnnotatedFieldInfo.getRpcFieldBindMap().values()) {

            ClassName fieldBindClassName = ClassName.get(rpcAnnotatedFieldInfo.packageName, fieldBind.getTargetTypeName());

            result.addStatement("object.$L = new $T(object)", fieldBind.name, fieldBindClassName);
        }
        return result.build();
    }

    /**
     * 生成解绑方法
     *
     * @param filer     Filer
     * @param className 所丰类类名
     * @return unbind方法描述
     */
    private static MethodSpec createUnbindMethod(RpcAnnotatedFieldInfo rpcAnnotatedFieldInfo, Filer filer, String className) {

        MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeVariableName.get("T"), "object", Modifier.FINAL)
                .addStatement("$T.unBind(object)", TypeName.get(RpcClientImpl.class));

        result.addJavadoc("\n@bind method was auto generated in $S.class.\n", className);

        for (RpcFieldBind fieldBind : rpcAnnotatedFieldInfo.getRpcFieldBindMap().values()) {

            result.addStatement("object.$L = null", fieldBind.name);
        }
        return result.build();
    }

}
