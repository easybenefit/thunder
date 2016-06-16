package thunder.network.impl;

/**
 * Created by whoislcj on 2015/11/4 - 11:11.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description:
 */

/**
 * 描述一个Void的返回类型
 *
 * @author cnoss
 */
public final class Void {

    volatile static Void instance = new Void();

    private Void() {
    }

    public static Void instance() {
        return instance;
    }
}
