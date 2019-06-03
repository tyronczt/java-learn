> - 在Java开发中，肯定会遇到构建HTTP请求的场景，常遇到的就是 **GET** 和 **POST** 
> - 以下总结三种常用的方法并实践之：**①HttpURLConnection**  **②HttpClient**  **③Hutool**

### 方法一、[HttpURLConnection](https://github.com/tyronczt/java-learn/blob/master/Tools/http-request/src/main/java/com/tyron/demo/http_client/Client_HttpURLConnection.java)
HttpURLConnection 是 Java 的标准类，是 Java 比较原生的一种实现方式。

**main方法测试**

```
public static void main(String[] args) {
      System.out.println("通过HttpURLConnection的get方式：" + doGet("http://localhost:8080/getReq"));
      Map<String, Object> map = new HashMap<>();
      map.put("name", "tyron");
      map.put("age", 18);
      System.out.println("通过HttpURLConnection的POST方式，参数为json形式：" + 
      doPost("http://localhost:8080/jsonPostReq", parseJson(map)));
      String param = "name=tyron";
      System.out.println("通过HttpURLConnection的POST方式，参数为String形式：" + 
      doPost("http://localhost:8080/stringPostReq", param));
  }

  /**
   * map转string
   *
   * @param map map
   * @return
   */
  public static String parseJson(Map<String, Object> map) {
      return JSONObject.toJSONString(map);
  }
```
**控制台输出**

```
通过HttpURLConnection的get方式：hello get

通过HttpURLConnection的POST方式，参数为json形式：用户：tyron,年龄：18

通过HttpURLConnection的POST方式，参数为String形式：hello：tyron
```

### [方法二：HttpClient4.5](https://github.com/tyronczt/java-learn/blob/master/Tools/http-request/src/main/java/com/tyron/demo/http_client/Client_HttpClient4.java)
HTTPClient 对 HTTP 的封装性比较不错，通过它基本上能够满足我们大部分的需求，HttpClient4.5 是 org.apache.http.client 下操作远程 url 的工具包

```
<!-- 引入依赖 -->
<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.5.3</version>
</dependency>
```

**main方法测试**
```
public static void main(String[] args) {
    System.out.println(doGet("http://localhost:8080/getReq"));
    Map<String, Object> map = new HashMap<>();
    map.put("name", "tyron");
    System.out.println(doPost("http://localhost:8080/stringPostReq", map));
}
```

**控制台输出**
```
DEBUG org.apache.http.client.protocol.RequestAddCookies - CookieSpec selected: default
DEBUG org.apache.http.client.protocol.RequestAuthCache - Auth cache not set in the context
DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager - Connection request: [route: {}->http://localhost:8080][total kept alive: 0; route allocated: 0 of 2; total allocated: 0 of 20]
DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager - Connection leased: [id: 0][route: {}->http://localhost:8080][total kept alive: 0; route allocated: 1 of 2; total allocated: 1 of 20]
DEBUG org.apache.http.impl.execchain.MainClientExec - Opening connection {}->http://localhost:8080
DEBUG org.apache.http.impl.conn.DefaultHttpClientConnectionOperator - Connecting to localhost/127.0.0.1:8080
DEBUG org.apache.http.impl.conn.DefaultHttpClientConnectionOperator - Connection established 127.0.0.1:10432<->127.0.0.1:8080
DEBUG org.apache.http.impl.conn.DefaultManagedHttpClientConnection - http-outgoing-0: set socket timeout to 60000
DEBUG org.apache.http.impl.execchain.MainClientExec - Executing request GET /getReq HTTP/1.1
。。。省略
Connection manager shut down
hello get
DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager - Connection request: [route: {}->http://localhost:8080][total kept alive: 0; route allocated: 0 of 2; total allocated: 0 of 20]
DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager - Connection leased: [id: 1][route: {}->http://localhost:8080][total kept alive: 0; route allocated: 1 of 2; total allocated: 1 of 20]
DEBUG org.apache.http.impl.execchain.MainClientExec - Opening connection {}->http://localhost:8080
DEBUG org.apache.http.impl.conn.DefaultHttpClientConnectionOperator - Connecting to localhost/127.0.0.1:8080
DEBUG org.apache.http.impl.conn.DefaultHttpClientConnectionOperator - Connection established 127.0.0.1:10433<->127.0.0.1:8080
DEBUG org.apache.http.impl.conn.DefaultManagedHttpClientConnection - http-outgoing-1: set socket timeout to 60000
DEBUG org.apache.http.impl.execchain.MainClientExec - Executing request POST /stringPostReq HTTP/1.1
。。。省略
DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager - Connection manager shut down
hello：tyron
```
### 方法三、Hutool - HttpUtil
- A set of tools that keep Java sweet。**Hutool**是一个Java工具包，也只是一个工具包，它帮助我们简化每一行代码，减少每一个方法，让Java语言也可以“甜甜的”。Hutool最初是我项目中“util”包的一个整理，后来慢慢积累并加入更多非业务相关功能，并广泛学习其它开源项目精髓，经过自己整理修改，最终形成丰富的开源工具集。
- 此处使用的是 Http客户端工具类 - **HttpUtil**

**main方法测试**

```
public static void main(String[] args) {
    // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
    String result1 = HttpUtil.get("http://localhost:8080/getReq");
    System.out.println(result1);

    // 当无法识别页面编码的时候，可以自定义请求页面的编码
    String result2 = HttpUtil.get("http://localhost:8080/getReq", CharsetUtil.CHARSET_UTF_8);
    System.out.println(result2);

    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put("name", "tyron");
    String result = HttpUtil.post("http://localhost:8080/stringPostReq", paramMap);
    System.out.println(result);

    Map<String, Object> map = new HashMap<>();
    map.put("name", "tyron");
    map.put("age", 18);
    System.out.println(HttpUtil.post("http://localhost:8080/jsonPostReq", JSONUtil.parseFromMap(map).toString()));
}
```
**控制台输出**
```
[main] DEBUG cn.hutool.log.LogFactory - Use [Slf4j] Logger As Default.
hello get
hello get
hello：tyron
用户：tyron,年龄：18
```