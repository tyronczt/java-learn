import static java.util.Calendar.*;

/**
 * Java 14 新特性
 */
public class Java14Test {

    public static void main(String[] args) throws Exception {
        /************************* Switch 表达式（正式） *******************************/
//        testSwitch();
        /**************************** 空指针异常信息增强 *******************************/
        testNullPointerException();
    }

    private static void testSwitch() {
        String day = "MON";
        String day2 = "WED";

        System.out.println(calculateLetters(day)); // 输出：1
        System.out.println(calculateLetters(day2));// 输出：3
    }

    private static int calculateLetters(String day) {
        return switch (day) {
            case "MON", "FRI" -> 1;
            case "TUES" -> 2;
            default -> day.length(); // 通过 yield 返回结果
        };
    }

    private static void testNullPointerException() {
        String str = null;
        try {
            System.out.println(str.length());
        } catch (NullPointerException e) {
            e.printStackTrace(); //  Cannot invoke "String.length()" because "str" is null
        }
    }
}

