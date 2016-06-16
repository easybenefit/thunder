基于编译期注解的Android Rpc框架
============

![Logo](http://www.yibenjiankang.com/pc/style/images/jiaru_banner.png)

说明描述

 * @Rpc,网络请求接口类类注解.
 * @RpcInterceptors, 类及方法拦截器注解，注解value必须是RpcInterceptor类。
 * @RpcInterceptor, 类及方法拦截器注解，注解value必须是RpcInterceptor类。
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
    void doGetUserInfoRequest(@RpcParam(name = "userId") int userId, 
						RpcServiceCallbackAdapter<UserInfo> callbackAdapter);

    @RpcApi(value = "/yb-api/user/modify", methodType = MethodType.PUT)
    void doPutUserInfoRequest(@RpcBody UserInfo userInfo, 
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

License
-------

    Copyright 2013 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

