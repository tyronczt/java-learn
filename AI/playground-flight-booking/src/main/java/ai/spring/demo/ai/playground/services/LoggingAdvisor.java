//package ai.spring.demo.ai.playground.services;
//
//import java.util.Map;
//
//import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
//import org.springframework.ai.chat.client.RequestResponseAdvisor;
//
//public class LoggingAdvisor implements RequestResponseAdvisor {
//
//    // 重写RequestResponseAdvisor接口的adviseRequest方法
//	@Override
//	public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
//        // 打印请求信息
//		System.out.println("Request: " + request);
//        // 返回原始请求对象
//		return request;
//	}
//
//    // 重写RequestResponseAdvisor接口的getOrder方法
//    @Override
//    public int getOrder() {
//        // 返回排序顺序，值越小优先级越高
//        return 0;
//    }
//}
