package com.tyron.collection.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Description: 5种方法删除list中的元素
 * @Author: tyron
 * @Date: Created in 2021/7/28
 */
public class RemoveElement {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("2");
        list.add("3");
        list.add("2");
        System.out.println("----修改前list----：" + list.toString());
//        testNoException(list);
//        testException(list);
//        testNoExceptionByIterator(list);
//        testNoExceptionByLambda(list);
//        test(list);
//        testByRemoveIf(list);
//        testByNewList(list);
        testByCopyOnWriteArrayList(list);
    }

    /**
     * https://blog.csdn.net/w605283073/article/details/103108761
     *
     * 当删除第一个元素时不会报错
     * 打印输出：
     * ----修改前list----：[1, 2]
     * ----testNoException修改后list----：[2]
     */
    private static void testNoException(List<String> list) {
        for (String item : list) {
            System.out.println("开始遍历" + item);
            if ("1".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println("----testNoException修改后list----：" + list.toString());
    }

    /**
     * 当删除第二个元素时报错
     * 打印输出：
     * ----修改前list----：[1, 2]
     * Exception in thread "main" java.util.ConcurrentModificationException
     * at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:909)
     * at java.util.ArrayList$Itr.next(ArrayList.java:859)
     * ···
     */
    private static void testException(List<String> list) {
        for (String item : list) {
            System.out.println("开始遍历" + item);
            if ("2".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println("----testException修改后list----：" + list.toString());
    }

    /**
     * 当删除第二个元素时报错
     * 打印输出：
     * ----修改前list----：[1, 2]
     * ----testNoExceptionByIterator修改后list----：[1]
     *
     * @param list
     */
    private static void testNoExceptionByIterator(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if ("2".equals(item)) {
                iterator.remove();
            }
        }
        System.out.println("----testNoExceptionByIterator修改后list----：" + list.toString());
    }

    private static void testNoExceptionByLambda(List<String> list) {
        List<String> collect = list.stream().filter(e -> !"2".equals(e)).collect(Collectors.toList());
        System.out.println("----testNoExceptionByLambda修改后list----：" + collect.toString());
    }

    /**
     * 直接使用简单for循环，以for (int i = 0; i < list.size(); i++) 进行遍历，这种方式可能会在遍历的过程中漏掉部分元素，从而出现少删的情况。
     * <p>
     * 通过简单的遍历方式，在遍历的过程中有可能会漏掉元素
     * 取第二个元素i=1时，满足条件被删掉，原有的数组的第三个元素，变成了新数组的第二个元素
     * i++后i=2,但i=2指向的是新数组中的第三个元素，那么原数组中的第三个元素就被漏掉了
     * https://segmentfault.com/a/1190000017215926
     * <p>
     * 逆向循环，是正确的
     * 1-->2-->3-->4
     * 逆向循环时，倒数第一个元素满足条件被删除时，i--后,原数组的倒数第二个变成了新数组的倒数第一个元素
     * i = size-2指向新数组的最后一个元素，没有漏掉。
     * 同理倒数第二个元素满足条件被删除时，i--后,原数组的倒数第三个变成了新数组的倒数第二个元素
     * i= size-3指向新数组的倒数第二个元素，也没有漏掉
     *
     * @param list
     */
    private static void test(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("2")) {
                System.out.println("第" + i + "个元素是：" + list.get(i));
                list.remove(list.get(i));
                i--;
            }
        }
//        for (int i = list.size() - 1; i >= 0; i--) {
//            if ("2".equals(list.get(i))) {
//                list.remove(i);
//            }
//        }
        System.out.println("----普通迭代修改后list----：" + list.toString());
    }

    private static void testByRemoveIf(List<String> list) {
        list.removeIf("2"::equals);
        System.out.println("----removeIf修改后list----：" + list.toString());
    }

    private static void testByNewList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            if (!s.equals("2")) {
                newList.add(s);
            }
        }
        System.out.println("----removeIf修改后list----：" + newList.toString());
    }

    /**
     * https://www.cnblogs.com/cxxjohnson/p/9056834.html
     *
     * @param list
     */
    private static void testByCopyOnWriteArrayList(List<String> list) {
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>(list);
        for (String item : cowList) {
            if (item.equals("2")) {
                cowList.remove(item);
            }
        }
        System.out.println("----CopyOnWriteArrayList修改后list----：" + cowList.toString());
    }
}
