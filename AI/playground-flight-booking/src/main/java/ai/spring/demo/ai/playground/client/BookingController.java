package ai.spring.demo.ai.playground.client;


import ai.spring.demo.ai.playground.services.BookingTools.BookingDetails;
import ai.spring.demo.ai.playground.services.FlightBookingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/")
public class BookingController {

	// 定义一个私有成员变量，用于存储FlightBookingService的实例
	private final FlightBookingService flightBookingService;

	// 构造函数，通过依赖注入的方式将FlightBookingService的实例注入到当前类中
	public BookingController(FlightBookingService flightBookingService) {
		this.flightBookingService = flightBookingService;
	}

	// 处理根路径的请求，返回视图名称"index"
	@RequestMapping("/")
	public String index() {
		return "index";
	}

	/**
	 * 处理获取所有预订详情的HTTP GET请求
	 * 
	 * 该函数通过Spring MVC框架映射到"/api/bookings"端点，返回当前系统中的航班预订信息列表。
	 * 数据通过FlightBookingService服务层获取，以JSON格式直接返回给客户端
	 * 
	 * @return List<BookingDetails> 包含所有预订详情的列表，每个元素包含单个预订的完整信息。
	 *         当没有预订时返回空列表（需确认服务层实现是否符合该约定）
	 */
	@RequestMapping("/api/bookings")
	@ResponseBody
	public List<BookingDetails> getBookings() {
	    return flightBookingService.getBookings();
	}

}
