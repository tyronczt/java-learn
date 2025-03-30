package ai.spring.demo.ai.playground.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import ai.spring.demo.ai.playground.data.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.NestedExceptionUtils;

@Configuration
public class BookingTools {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    // 定义日志记录器
    private static final Logger logger = LoggerFactory.getLogger(BookingTools.class);

    // 自动注入FlightBookingService
    @Autowired
    private FlightBookingService flightBookingService;

    // 定义机票预定详情请求记录
    public record BookingDetailsRequest(String bookingNumber, String name) {
    }

    // 定义修改机票预定日期请求记录
    public record ChangeBookingDatesRequest(String bookingNumber, String name, String date, String from, String to) {
    }

    // 定义取消机票预定请求记录
    public record CancelBookingRequest(String bookingNumber, String name) {
    }

    /**
     * 创建用于获取机票预定日期的函数Bean
     *
     * @param name 乘客姓名
     * @param date 预定日期
     * @param from 出发地
     * @param to   目的地
     */
    public record BookingDatesRequest(String bookingNumber, String name, String date, String from, String to) {
    }

    // 定义机票预定详情响应记录，包含非空字段
    @JsonInclude(Include.NON_NULL)
    public record BookingDetails(String bookingNumber, String name, LocalDate date, BookingStatus bookingStatus,
                                 String from, String to, String bookingClass) {
    }

    /**
     * 创建用于获取机票预订详细信息的函数Bean
     *
     * @return Function<BookingDetailsRequest, BookingDetails> 接收预订请求参数，返回预订详情的函数
     * 参数说明：
     * BookingDetailsRequest - 包含预订编号和乘客姓名的请求对象
     * 返回值说明：
     * BookingDetails - 包含航班号、舱位、日期等详细信息的响应对象
     * 异常处理：
     * 当查询出现异常时，记录具体错误信息并返回基础信息响应对象（不含航班详情）
     */
    @Bean
    @Description("获取机票预定详细信息")
    public Function<BookingDetailsRequest, BookingDetails> getBookingDetails() {
        return request -> {
            // 尝试获取完整预订信息
            try {
                return flightBookingService.getBookingDetails(request.bookingNumber(), request.name());

                // 异常处理逻辑：记录错误并返回基础响应
            } catch (Exception e) {
                logger.warn("Booking details: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
                // 构造仅含基本信息的响应对象（航班详情字段均为null）
                return new BookingDetails(request.bookingNumber(), request.name(), null, null, null, null, null);
            }
        };
    }

    /**
     * 创建修改机票预定日期的函数Bean
     *
     * @param request 包含预定修改参数的请求对象，参数包括：
     *                - bookingNumber: 需要修改的预定编号
     *                - name: 预定人姓名（用于验证身份）
     *                - date: 新的出发日期
     *                - from: 出发地
     *                - to: 目的地
     * @return Function<ChangeBookingDatesRequest, String> 处理航班预定日期修改请求的函数
     * 函数接收ChangeBookingDatesRequest参数，返回空字符串表示操作完成
     */
    @Bean
    @Description("修改机票预定日期")
    public Function<ChangeBookingDatesRequest, String> changeBooking() {

        return request -> {
            try {
                final LocalDate currentDate = LocalDate.now(TIME_ZONE);
                final String requestDateStr = request.date();
                // 参数有效性校验
                if (Strings.isNullOrEmpty(request.bookingNumber())) {
                    return "预定编号不能为空";
                }
                if (Strings.isNullOrEmpty(request.name())) {
                    return "乘客姓名不能为空";
                }
                final LocalDate requestDate = LocalDate.parse(requestDateStr, DATE_FORMATTER);
                if (requestDateStr.isBlank() || requestDate.isBefore(currentDate)) {
                    return "出发日期不合法";
                }
                if (Strings.isNullOrEmpty(request.from()) || Strings.isNullOrEmpty(request.to())) {
                    return "出发地/目的地不能为空";
                }

                // 执行核心业务
                flightBookingService.changeBooking(
                        request.bookingNumber(),
                        request.name(),
                        request.date(),
                        request.from(),
                        request.to());

                return "";
            } catch (IllegalArgumentException e) {
                return "参数校验失败: " + e.getMessage();
            } catch (Exception e) {
                // 记录系统级异常日志
                logger.error("修改预定日期失败", e);
                return "系统处理异常，请稍后再试";
            }
        };
    }

    // 定义取消机票预定的Bean
    @Bean
    @Description("取消机票预定")
    public Function<CancelBookingRequest, String> cancelBooking() {
        return request -> {
            // 调用服务取消机票预定
            flightBookingService.cancelBooking(request.bookingNumber(), request.name());
            // 返回空字符串
            return "";
        };
    }

    /**
     * 创建机票预定处理函数Bean
     * <p>该函数接收预定请求对象，执行参数校验和业务处理，返回处理结果消息。</p>
     * @return Function<BookingDatesRequest, String> 处理函数，参数为预定请求对象，返回处理结果：
     *         - 空字符串表示预定成功
     *         - 非空字符串表示错误信息（包含参数校验错误、系统错误）
     * @throws IllegalArgumentException 当请求参数校验不通过时抛出
     */
    @Bean
    @Description("机票预定")
    public Function<BookingDatesRequest, String> booking() {
        return request -> {
            try {
                // 基础参数校验块
                if (Strings.isNullOrEmpty(request.name())) {
                    return "乘客姓名不能为空";
                }
                if (Strings.isNullOrEmpty(request.from()) || Strings.isNullOrEmpty(request.to())) {
                    return "出发地/目的地不能为空";
                }
                // 核心业务处理：执行航班预定操作
                flightBookingService.booking(request.name(),request.date(),request.from(),request.to());
                return "";
            } catch (IllegalArgumentException e) {
                // 业务校验异常处理：返回可读的错误信息
                return "参数校验失败: " + e.getMessage();
            } catch (Exception e) {
                // 系统异常处理：记录详细日志并返回统一错误提示
                logger.error("预定失败", e);
                return "系统处理异常，请稍后再试";
            }
        };
    }


}
