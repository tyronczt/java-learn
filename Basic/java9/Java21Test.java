import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Java 21 新特性
 */
public class Java21Test {

    public static void main(String[] args) throws Exception {
        /************************* 增强的序列集合（Sequenced Collections） *******************************/
//        testCollection();
        /************************* 记录模式（Record Patterns） *******************************/
//        var shape = new Circle(new Point(1, 2), 3.0);
//        processShape(shape);
//        System.out.println(describe(shape));
//        checkBox(new Box<>("hello"));
//        guardedPattern(new Point(1, 1));
        /************************* switch的模式匹配  *******************************/
//        testSwitch("Hello, Java!");
        /************************* 虚拟线程（Virtual Threads）  *******************************/
        testVirtualThread();
    }

    private static void testCollection() {
        List<String> list = new ArrayList<>(List.of("B", "C"));
        SequencedCollection<String> seqList = list;
        seqList.addFirst("A");  // list 变为 [A, B, C]
        System.out.println(seqList.toString());
        seqList.removeLast();   // list 变为 [A, B]
        System.out.println(seqList.toString());
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        SequencedMap<String, Integer> seqMap = map;
        seqMap.putFirst("Key1", 1);  // 插入到 Map 开头
        System.out.println(seqMap.toString());
        seqMap.pollLastEntry();       // 移除最后一个条目
        System.out.println(seqMap.toString());
    }

    // 定义两个记录类
    record Point(int x, int y) {

    }
    record Circle(Point center, double radius) {

    }
    // 使用记录模式
    private static void processShape(Object shape) {
        if (shape instanceof Circle(Point p, double r)) { // 解构嵌套记录
            System.out.printf("圆心坐标 (%d,%d), 半径 %.2f%n", p.x(), p.y(), r);
        }
    }

    // 在 switch 中使用
    private static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) -> "点坐标: (" + x + "," + y + ")";
            case Circle(Point p, double r) -> "圆形半径: " + r;
            default -> "未知形状";
        };
    }

    /**
     * 结合泛型记录
      * @param content
     */
    record Box<T>(T content) {

    }
    static void checkBox(Box<String> box) {
        if (box instanceof Box<String>(var s)) {
            System.out.println("字符串内容: " + s.toUpperCase());
        }
    }

    /**
     * 带守卫条件的模式
     */
    static void guardedPattern(Object obj) {
        if (obj instanceof Point(int x, int y) && x == y) {
            System.out.println("坐标点在 y=x 直线上");
        }
    }

    private static void testSwitch(Object obj) {
        String result = switch (obj) {
            case String s -> "String: " + s;
            case Integer i -> "Integer: " + i;
            default -> "Unknown type";
        };
        System.out.println(result);
    }

    private static void testVirtualThread() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                System.out.println("Hello from virtual thread!");
            });
        }
        executor.shutdown();
    }
}

