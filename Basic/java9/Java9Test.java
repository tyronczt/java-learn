import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Java 9 新特性
 */
public class Java9Test {

    public static void main(String[] args) {
        /************************* List.of() 方法为不可变集合 **************************/
//        testListOf();

        /************************** Stream API 增强 **********************************/
//        testStreamApi();

        /************************** Optional API 增强 **********************************/
//        testOptional();

        /************************** 安全算法 **********************************/
        testSHA();
    }

    /**
     * List.of() 方法为不可变集合
     */
    private static void testListOf() {
        List<String> list = List.of("Java", "Kotlin", "Scala");
        Set<Integer> set = Set.of(1, 2, 3);
        Map<String, Integer> map = Map.of("A", 1, "B", 2);
        // 尝试修改会抛出 UnsupportedOperationException
        list.add("C++");  // 报错！
    }

    /**
     * Stream API 增强
     * Stream 中增加了新的方法 ofNullable()、dropWhile()、takeWhile() 以及 iterate() 方法的重载方法。
     */
    private static void testStreamApi() {
        Stream<String> stringStream = Stream.ofNullable("Java");
        System.out.println(stringStream.count());// 1
        Stream<String> nullStream = Stream.ofNullable(null);
        System.out.println(nullStream.count());//0

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 4, 3, 2, 1);
        List<Integer> taken = numbers.stream()
                .takeWhile(n -> n < 5) // 取元素直到 n >=5
                .collect(Collectors.toList());
        System.out.println(taken.toString()); // [1, 2, 3, 4]
        List<Integer> dropped = numbers.stream()
                .dropWhile(n -> n < 5)  // 丢弃元素直到 n >=5
                .collect(Collectors.toList());
        System.out.println(dropped.toString());// [5, 4, 3, 2, 1]

        IntStream.iterate(3, x -> x < 10, x -> x + 3).forEach(System.out::println); // [3,6,9]
    }

    private static void testOptional() {
        // 测试 ifPresentOrElse
        Optional<String> optional = Optional.of("TestIfPresentOrElse");
        optional.ifPresentOrElse(x -> System.out.println("Value: " + x),
                () -> System.out.println("Not Present.")); // Value: TestIfPresentOrElse
        optional = Optional.empty();
        optional.ifPresentOrElse(x -> System.out.println("Value: " + x),
                () -> System.out.println("Not Present.")); // Not Present.

        // 测试 stream
        List<Optional<String>> list = Arrays.asList(Optional.empty(), Optional.of("A"), Optional.empty(), Optional.of("B"));

        //Optional::stream method will return a stream of either one or zero element if data is present or not.
        List<String> filteredListJava9 = list.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        System.out.println(filteredListJava9); // [A, B]

        // 测试or
        Optional<String> optional1 = Optional.of("testOr");
        Supplier<Optional<String>> supplierString = () -> Optional.of("Not Present");
        optional1 = optional1.or(supplierString);
        optional1.ifPresent(x -> System.out.println("Value: " + x)); // Value: testOr
        optional1 = Optional.empty();
        optional1 = optional1.or(supplierString);
        optional1.ifPresent(x -> System.out.println("Value: " + x));// Value: Not Present
    }

    private static void testSHA() {
        final MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA3-224");
            final byte[] digest = instance.digest("111".getBytes());
            System.out.println(Hex.encodeHexString(digest)); // 9b8c0b84c1ed4c649aca41d733cc2dddb706daba449688da33405abe
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
