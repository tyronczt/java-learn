import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 12 新特性
 */
public class Java12Test {

    public static void main(String[] args) throws Exception {
        /************************* Switch 表达式（预览） *******************************/
//        testSwitch();
        /**************************** 字符串增强 **************************************/
//        testString();
        /**************************** 紧凑数字格式化 ***********************************/
//        testCNF();
        /****************************** 文件内容比对 ***********************************/
//        testFileMismatch();
        /****************************** Teeing Collector ***********************************/
        testTeeingCollector();
    }

    private static void testSwitch() {
        int day = 3;
        String dayType = switch (day) {
            case 1, 2, 3, 4, 5 -> "工作日";
            case 6, 7 -> "周末";
            default -> throw new IllegalArgumentException("无效的日期: " + day);
        };
        System.out.println(dayType); // 输出: 工作日
    }

    private static void testString() {
        String text = "Hello\nJava\n12";
        // 缩进每行 2 个空格
        System.out.println(text.indent(2)); // 输出: [Hello Java 12]

        // 转换为大写
        String upper = text.transform(String::toUpperCase);
        System.out.println(upper); // 输出: [HELLO JAVA 12]
    }

    private static void testCNF() {
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        System.out.println(fmt.format(1000));     // 1K
        System.out.println(fmt.format(1_000_000)); // 1M
    }

    private static void testFileMismatch() throws IOException {
        Path file1 = Files.createTempFile("file1", ".txt");
        Path file2 = Files.createTempFile("file2", ".txt");
        Files.writeString(file1, "Java 12");
        Files.writeString(file2, "Java 12!");

        long mismatch = Files.mismatch(file1, file2);
        System.out.println(mismatch); // 输出: 7（在第7个字符处不匹配）
    }

    /**
     * 测试并演示Collectors.teeing收集器的使用，计算整数流的平均值。<br>
     * 通过组合两个下游收集器（summingDouble和counting），使用合并函数计算最终结果。<br>
     * 函数流程：
     * 1. 创建包含10/20/30的整数流
     * 2. 使用teeing收集器同时执行两种归约操作：
     *    - summingDouble收集器计算总和
     *    - counting收集器统计元素数量
     * 3. 通过BiFunction将总和除以数量得到平均值
     * 无参数
     * 无返回值
     */
    private static void testTeeingCollector() {
        double avg = Stream.of(10, 20, 30)
                .collect(Collectors.teeing(
                        Collectors.summingDouble(Integer::intValue),
                        Collectors.counting(),
                        (sum, count) -> sum / count
                ));
        System.out.println(avg); // 输出: 20.0
    }
}
