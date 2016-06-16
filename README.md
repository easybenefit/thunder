Thunder网络
============

![Logo](http://www.yibenjiankang.com/pc/style/images/jiaru_banner.png)

基于编译期注解的Android网络请求开源框架。

 * Eliminate `findViewById` calls by using `@BindView` on fields.
 * Group multiple views in a list or array. Operate on all of them at once with actions,
   setters, or properties.
 * Eliminate anonymous inner-classes for listeners by annotating methods with `@OnClick` and others.
 * Eliminate resource lookups by using resource annotations on fields.

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





 [1]: http://square.github.com/dagger/
 [2]: https://search.maven.org/remote_content?g=com.jakewharton&a=butterknife&v=LATEST
 [3]: http://jakewharton.github.com/butterknife/
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/