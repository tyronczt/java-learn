package ai.spring.demo.ai.playground.client;

import ai.spring.demo.ai.playground.services.CustomerSupportAssistant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RequestMapping("/api/assistant")
@RestController
public class AssistantController {

	// 定义一个私有的final类型的CustomerSupportAssistant对象，用于处理聊天功能
	private final CustomerSupportAssistant agent;

	// 构造函数，通过依赖注入的方式将CustomerSupportAssistant对象传递给agent
	public AssistantController(CustomerSupportAssistant agent) {
		this.agent = agent;
	}

	// 定义一个处理GET请求的方法，路径为/api/assistant/chat，返回类型为Flux<String>
	@RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> chat(String chatId, String userMessage) {
		// 调用agent的chat方法，传入chatId和userMessage，返回一个Flux<String>对象
		return agent.chat(chatId, userMessage);
	}
}
