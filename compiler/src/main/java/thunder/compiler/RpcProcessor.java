package thunder.compiler;

import com.google.auto.service.AutoService;
import thunder.annotations.*;
import thunder.network.util.MessageUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by HandGunBreak on 2016/4/5 - 18:36.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: Processor类
 */
@SuppressWarnings("unused")
@AutoService(Processor.class)
public class RpcProcessor extends AbstractProcessor {

    private ProcessingEnvironment mProcessingEnvironment;
    private static LinkedHashSet<Class<? extends Annotation>> mOptionalAnnotationHashSet = new LinkedHashSet<>();

    static {

        mOptionalAnnotationHashSet.add(PreferBind.class);
    }

    /**
     * 初始化，系统调用
     *
     * @param processingEnvironment 编译期解析环境
     */
    @Override
    public void init(ProcessingEnvironment processingEnvironment) {

        super.init(processingEnvironment);
        this.mProcessingEnvironment = processingEnvironment;
        MessageUtil.setMessageUtil(processingEnvironment.getMessager());
    }

    /**
     * 编译期注解解析方法
     *
     * @param annotations 被注解元素集
     * @param roundEnv    env
     * @return 处理结果
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        RpcAnnotationParser.getInstance().parser(roundEnv, mProcessingEnvironment.getFiler(), mProcessingEnvironment.getTypeUtils(), mProcessingEnvironment.getElementUtils());
        return true;
    }

    /**
     * 定义支持注解类型
     *
     * @return 注解类型集
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(Rpc.class.getCanonicalName());
        annotationTypes.add(RpcApi.class.getCanonicalName());
        annotationTypes.add(RpcUrl.class.getCanonicalName());
        annotationTypes.add(RpcBody.class.getCanonicalName());
/*        annotationTypes.add(RpcScope.class.getCanonicalName());*/
        annotationTypes.add(RpcParam.class.getCanonicalName());
        annotationTypes.add(RpcInterceptor.class.getCanonicalName());
        annotationTypes.add(RpcInterceptors.class.getCanonicalName());
        for (Class<? extends Annotation> clazz : mOptionalAnnotationHashSet) {

            annotationTypes.add(clazz.getCanonicalName());
        }
        return annotationTypes;
    }

    /**
     * 支持注解类型编译版本号
     *
     * @return 版本号
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.latest();
    }

}
