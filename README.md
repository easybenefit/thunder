Thunder网络框架
============

![Logo](http://www.yibenjiankang.com/pc/style/images/jiaru_banner.png)

基于编译期注解的Android网络请求开源框架。

 * @Rpc,网络请求接口类类注解.
 * @RpcInterceptors, 类及及方式注解，注解value必须是RpcInterceptor类。
 * @RpcInterceptor, 类及及方式注解，注解value必须是RpcInterceptor类。
 * @RpcParam，参数注解
 * @RpcBody， 参数对象注解
 
```java
class ThunderApplication extends Application {

@Override public void onCreate() {        
	super.onCreate();
	RpcClientManager.addGlobalInterceptor(new HeaderInterceptor());
	RpcClientManager.addGlobalInterceptor(new DeviceInfoInterceptor());
	RpcClientManager.setApiDomain(url);
	RpcClientManager.setApiPort(port);
	RpcClientManager.setSecApiPort(secPort);
}
```

```java
@Rpc
@RpcInterceptors({
	@RpcInterceptor(DeviceInterceptor.class),
	@RpcInterceptor(SignInterceptor.class)
})
public interface UserApi {


    @RpcApi("/yb-api/user/info")
    void doGetUserInfo(@RpcParam(name = "userId") int userId, 
						RpcServiceCallbackAdapter<UserInfo> callbackAdapter);

    @RpcApi("/yb-api/user/modify")
    void doGetUserInfo(@RpcBody UserInfo userInfo, 
						RpcServiceCallbackAdapter<UserInfo> callbackAdapter);
  }
```

```java
class ThunderActivity extends Activity {

  @RpcService UserApi mUserApi;
  @RpcService LoginApi mLoginApi;

  @Override public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    Thunder.bind(this);
    // TODO Use RpcService fields...
  }

  @Override public void onDestroy(){

	super.onDestroy();
	Thunder.unbind(this);
  }
```





 [1]: http://www.yibenjiankang.com/pc/jiaru.html
