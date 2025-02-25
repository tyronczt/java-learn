/**
 * Java 16 新特性
 */
public class Java16Test {

    public static void main(String[] args) throws Exception {
        /************************* 记录类（Records，JEP 395 正式特性） *******************************/
        testRecord();

    }

    /**
     * 自动生成构造方法、equals、hashCode 等
     */
    public record Person(String name, int age) {}

    private static void testRecord() {
        Person p = new Person("Alice", 30);
        System.out.println(p.name()); // 输出 Alice
        System.out.println(p);        // 输出 Person[name=Alice, age=30]
    }

}

