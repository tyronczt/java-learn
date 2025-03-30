package ai.spring.demo.ai.playground.services;

import ai.spring.demo.ai.playground.data.*;
import ai.spring.demo.ai.playground.services.BookingTools.BookingDetails;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class FlightBookingService {

    // 定义一个BookingData类型的成员变量db，用于存储客户和预订数据
    private final BookingData db;

    // 构造函数，初始化db并调用initDemoData方法初始化演示数据
    public FlightBookingService() {
        db = new BookingData();

        initDemoData();
    }

    // 初始化演示数据的方法
    private void initDemoData() {
        // 定义客户名称列表
        List<String> names = List.of("林晚照", "苏逸尘", "林悦澄", "陈景铄", "柳念恩");
        // 定义机场代码列表
        List<String> airportCodes = List.of("北京", "上海", "广州", "深圳", "杭州", "南京", "青岛", "成都", "武汉", "西安", "重庆", "大连",
                "天津");
        // 创建一个随机数生成器
        Random random = new Random();

        // 创建客户和预订的列表
        var customers = new ArrayList<Customer>();
        var bookings = new ArrayList<Booking>();

        // 循环生成5个客户和预订
        for (int i = 0; i < 5; i++) {
            String name = names.get(i);
            String from = airportCodes.get(random.nextInt(airportCodes.size()));
            String to = airportCodes.get(random.nextInt(airportCodes.size()));
            BookingClass bookingClass = BookingClass.values()[random.nextInt(BookingClass.values().length)];
            Customer customer = new Customer();
            customer.setName(name);

            LocalDate date = LocalDate.now().plusDays(2 * (i + 1));

            Booking booking = new Booking("10" + (i + 1), date, customer, BookingStatus.CONFIRMED, from, to,
                    bookingClass);
            customer.getBookings().add(booking);

            customers.add(customer);
            bookings.add(booking);
        }

        // Reset the database on each start
        db.setCustomers(customers);
        db.setBookings(bookings);
    }

    /**
     * 定义一个公共方法，返回一个包含预订详情的列表
     */
    public List<BookingDetails> getBookings() {
        return db.getBookings().stream().map(this::toBookingDetails).toList();
    }

    /**
     * 根据预订号和客户名称查找预订
     */

    private Booking findBooking(String bookingNumber, String name) {
        return db.getBookings()
                .stream()
                .filter(b -> b.getBookingNumber().equalsIgnoreCase(bookingNumber))
                .filter(b -> b.getCustomer().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    /**
     * 获取指定预订的详细信息
     */
    public BookingDetails getBookingDetails(String bookingNumber, String name) {
        var booking = findBooking(bookingNumber, name);
        return toBookingDetails(booking);
    }

    /**
     * 修改已存在的预订信息
     *
     * @param newDate 新出发日期（格式：yyyy-MM-dd）
     * @throws IllegalArgumentException 如果出发时间在24小时内禁止修改
     */
    public void changeBooking(String bookingNumber, String name, String newDate, String from, String to) {
        // 根据预订号和姓名查找预订信息
        var booking = findBooking(bookingNumber, name);
        // 检查新的出发日期是否在当前日期的24小时内
        if (booking.getDate().isBefore(LocalDate.now().plusDays(1))) {
            // 如果在24小时内，抛出非法参数异常
            throw new IllegalArgumentException("Booking cannot be changed within 24 hours of the start date.");
        }
        // 设置新的出发日期
        booking.setDate(LocalDate.parse(newDate));
        // 设置新的出发地
        booking.setFrom(from);
        // 设置新的目的地
        booking.setTo(to);
    }

    /**
     * 新增预定
     * 该方法会创建客户信息，生成预定记录，并将数据持久化到数据库<br>
     * 注意：本方法会同时修改客户表和预定表的数据
     * 
     * @param name 客户姓名，不能为空
     * @param bookingDate 预定日期字符串，格式应符合ISO_LOCAL_DATE规范（yyyy-MM-dd）
     * @param from 出发地/起始位置
     * @param to 目的地/结束位置
     */
    public void booking(String name, String bookingDate, String from, String to) {
        // 创建客户对象并设置基础属性
        Customer customer = new Customer();
        customer.setName(name);

        // 构建预定对象，包含业务关键要素：
        // - 自动生成UUID作为唯一标识
        // - 固定使用BUSINESS舱位等级
        // - 预定状态初始化为CONFIRMED
        Booking booking = new Booking("106", LocalDate.parse(bookingDate), customer, BookingStatus.CONFIRMED, from, to,
                BookingClass.BUSINESS);

        // 更新数据库客户表
        List<Customer> customers = db.getCustomers();
        customers.add(customer);
        db.setCustomers(customers);

        // 更新数据库预定表
        List<Booking> bookings = db.getBookings();
        bookings.add(booking);
        db.setBookings(bookings);
    }


    /**
     * 取消指定预订（核心业务逻辑）
     * 业务规则：出发时间48小时内不可取消
     *
     * @throws IllegalArgumentException 当违反取消时间规则时抛出
     */
    public void cancelBooking(String bookingNumber, String name) {
        // 根据预订号和姓名查找预订信息
        var booking = findBooking(bookingNumber, name);
        // 检查预订的出发日期是否在当前日期的48小时（2天）内
        if (booking.getDate().isBefore(LocalDate.now().plusDays(2))) {
            // 如果在48小时内，抛出非法参数异常
            throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
        }
        // 如果不在48小时内，设置预订状态为已取消
        booking.setBookingStatus(BookingStatus.CANCELLED);
    }


    /**
     * 将Booking对象转换为BookingDetails对象
     * 该方法接收一个Booking对象作为参数，并返回一个新的BookingDetails对象。
     * BookingDetails对象包含了Booking对象中的关键信息，如预订号、客户名称、预订日期、预订状态、出发地、目的地和预订舱位等级。
     */
    private BookingDetails toBookingDetails(Booking booking) {
        return new BookingDetails(booking.getBookingNumber(), booking.getCustomer().getName(), booking.getDate(),
                booking.getBookingStatus(), booking.getFrom(), booking.getTo(), booking.getBookingClass().toString());
    }

}
