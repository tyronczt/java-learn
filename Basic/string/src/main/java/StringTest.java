import java.lang.reflect.Field;

/**
 * @Description: 测试string
 * @Author: tyron
 * @Date: Created in 2021/8/21
 */
public class StringTest {
    public static void main(String[] args) {
//        String str = "Tyron";
//        System.out.println("反射前：" + str);
//        try {
//            Field field = String.class.getDeclaredField("value");
//            field.setAccessible(true);
//            char[] value = (char[]) field.get(str);
//            value[0] = 't';
//            System.out.println("反射后：" + str);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }


        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = "Hel" + "lo";
        String s4 = "Hel" + new String("lo");
        String s5 = new String("Hello");
        String s6 = s5.intern();
        String s7 = "H";
        String s8 = "ello";
        String s9 = s7 + s8;

        System.out.println(s1 == s2);       // true
        System.out.println(s1 == s3);       // true
        System.out.println(s1 == s4);       // false
        System.out.println(s1.equals(s4));  // true
        System.out.println(s1 == s9);       // false
        System.out.println(s1.equals(s9));  // true
        System.out.println(s1 == s5);       // false
        System.out.println(s1.equals(s5));  // true
        System.out.println(s1 == s6);       // true
    }
}
