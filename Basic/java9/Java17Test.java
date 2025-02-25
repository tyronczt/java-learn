import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Java 17 新特性
 */
public class Java17Test {

    public static void main(String[] args) throws Exception {
        /************************* 增强的伪随机数生成器 *******************************/
        testRandom();
    }

    private static void testRandom() {
        // 获取算法为 L32X64MixRandom 的生成器
        RandomGenerator generator = RandomGeneratorFactory.of("L32X64MixRandom").create();

        // 生成 0~99 的随机数
        int num = generator.nextInt(100);
        System.out.println("随机数: " + num);
    }
}

