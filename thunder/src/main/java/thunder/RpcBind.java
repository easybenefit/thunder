package thunder;

/**
 * Created by HandGunBreak on 2016/4/13 - 16:27.
 * Mail: handgunbreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2015-2016)
 * Description:
 */
public interface RpcBind<T> {

    void bind(T object);

    void unbind(T object);
}
