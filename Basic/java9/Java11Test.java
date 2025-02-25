import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Java 11 新特性
 */
public class Java11Test {

    public static void main(String[] args) throws Exception {
        /************************* HTTP Client 标准化 *******************************/
//        testHttpClient();
        /************************* String 增强 *************************************/
//        testString();
        /************************* Lambda 参数局部变量类型推断 ***********************/
//        testLambda();
        /**************************** Optional.isEmpty() **************************/
//        testOptional();
        /******************* Files.readString()和Files.writeString() **************/
//        testFile();
        /*************************** Unicode 10 支持 *******************************/
//        testUnicode();
        /********************** ChaCha20-Poly1305 加密算法 ***************************/
        testChaCha20Poly1305();
    }

    private static void testHttpClient() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get"))
                .build();
        // 同步请求
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("同步响应状态码: " + response.statusCode()); // 同步响应状态码: 200
        // 异步请求
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }

    private static void testString() {
        String str = "  Hello\u2005Java 11  ";

        // 去除首尾Unicode空白字符
        System.out.println("strip(): [" + str.strip() + "]"); // strip(): [HelloJava 11]

        //去除字符串首部空格
        System.out.println(" Java ".stripLeading());   // "Java "

        //去除字符串尾部空格
        System.out.println(" Java ".stripTrailing());  // " Java"

        // 判断是否为空或仅包含空白字符
        System.out.println("isBlank(): " + "   ".isBlank()); // isBlank(): true

        // 按行分割为流
        "Line1\nLine2\r\nLine3".lines().forEach(System.out::println); //  [Line1 , Line2, Line3]

        //重复字符串多少次
        System.out.println("Java".repeat(3)); // "JavaJavaJava"
    }

    private static void testLambda() {
        // 使用 var 声明 Lambda 参数类型
        BiFunction<String, Integer, String> formatter =
                (var str, var num) -> String.format("%s: %d", str, num);

        System.out.println(formatter.apply("Age", 25)); // Age: 25
    }

    private static void testOptional() {
        var op = Optional.empty();
        System.out.println(op.isEmpty()); //判断指定的 Optional 对象是否为空 true
    }

    private static void testFile() throws IOException, URISyntaxException {
        //Read file as string
        URI txtFileUri = Java11Test.class.getClassLoader().getResource("hello_world.txt").toURI();

        String content = Files.readString(Path.of(txtFileUri), Charset.defaultCharset());

        //Write string to file
        Path tmpFilePath = Path.of(File.createTempFile("tempFileTest", ".tmp").toURI());

        Path returnedFilePath = Files.writeString(tmpFilePath,"Hello World!",
                Charset.defaultCharset(), StandardOpenOption.WRITE);
    }

    /**
     * Unicode 10.0.0 引入了许多新的字符，其中包含了各种符号、表情符号、emoji 等。以下是一些 Unicode 10 的示例字符：
     * <p>
     * ⭐ (U+2B50) - 星星符号
     * 🌙 (U+1F319) - 月亮符号
     * 🎉 (U+1F389) - 庆祝符号
     * 🐼 (U+1F43C) - 熊猫表情符号
     * 🌹 (U+1F339) - 玫瑰花符号
     * 🍕 (U+1F355) - 披萨符号
     * 🚀 (U+1F680) - 火箭符号
     * 💡 (U+1F4A1) - 电灯泡符号
     * 🎵 (U+1F3B5) - 音符符号
     * 🏆 (U+1F3C6) - 奖杯符号
     *
     * @param args
     */
    private static void testUnicode() {
        // Unicode 10 的一些示例字符
        char unicodeChar = '⭐'; // 星星符号
        System.out.println("Unicode Character: " + unicodeChar);

        // 字符串中的 Unicode 10 字符
        String unicodeString = "Java 11 supports Unicode 10.0.0 🎉";
        System.out.println("Unicode String: " + unicodeString);

        // 获取字符的 Unicode 编码点
        int codePoint = unicodeChar;
        System.out.println("Unicode Code Point of Character: " + codePoint);

        // 检查字符是否为 Unicode 字母
        boolean isLetter = Character.isLetter(unicodeChar);
        System.out.println("Is Character a Unicode Letter? " + isLetter);
    }

    private static void testChaCha20Poly1305() throws Exception {
        Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
        KeyGenerator kg = KeyGenerator.getInstance("ChaCha20");
        SecretKey key = kg.generateKey();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal("SecretData".getBytes());
        System.out.println(Arrays.toString(encrypted));
    }
}
