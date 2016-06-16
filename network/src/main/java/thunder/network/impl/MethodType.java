package thunder.network.impl;

/**
 * Created by HandGunBreak on 2016/5/15 - 12:07.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
public class MethodType {

    /**
     * 对应于REST中的GET操作，通常用来获取HTTP的服务资源
     */
    public final static int GET = 0x100;
    /**
     * 对应于REST中的PUT操作，通常用来更新/修改HTTP的资源
     */
    public final static int PUT = 0x200;
    /**
     * 对应于REST中的GET操作，通常用来创建新的HTTP的服务资源
     */
    public final static int POST = 0x300;
    /**
     * 对应于REST中的GET操作，通常用来删除对应的HTTP服务资源
     */
    public final static int DELETE = 0x400;
}
