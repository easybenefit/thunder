package thunder.annotations;

/**
 * Created by Gary_Cheung on 15/10/30.
 */
class MethodType {
    /**
     * 对应于REST中的GET操作，通常用来获取HTTP的服务资源
     */
    final static int GET = 0x100;
    /**
     * 对应于REST中的PUT操作，通常用来更新/修改HTTP的资源
     */
    final static int PUT = 0x200;
    /**
     * 对应于REST中的GET操作，通常用来创建新的HTTP的服务资源
     */
    final static int POST = 0x300;
    /**
     * 对应于REST中的GET操作，通常用来删除对应的HTTP服务资源
     */
    final static int DELETE = 0x400;
}
