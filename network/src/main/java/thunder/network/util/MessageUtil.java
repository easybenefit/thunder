package thunder.network.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

/**
 * Created by  HandGunBreak  on 2016/4/7 - 18:13.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description: 消息打印工具类
 */
public class MessageUtil {

    private static Messager messager;

    public static void setMessageUtil(Messager messager) {

        MessageUtil.messager = messager;
    }

    /**
     * 打印错误消息
     *
     * @param element 程序元素
     * @param message 格式化消息内容
     * @param args    参数
     */
    public static void error(Element element, String message, Object... args) {

        if (messager != null) {

            if (args.length > 0) {

                message = String.format(message, args);
            }
            messager.printMessage(Kind.ERROR, message, element);
        }
    }

    /**
     * 打印警告消息
     *
     * @param element 程序元素
     * @param message 格式化消息内容
     * @param args    参数
     */
    public static void warning(Element element, String message, Object... args) {

        if (messager != null) {

            if (args.length > 0) {

                message = String.format(message, args);
            }
            messager.printMessage(Kind.WARNING, message, element);
        }
    }

}
