package ai.spring.demo.ai.playground;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;


@SpringBootApplication
public class Application  {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).run(args);
	}

	/**
	 * 创建并返回一个CommandLineRunner Bean，用于在应用启动时将服务条款文档注入向量数据库，
	 * 并执行相似性搜索演示。
	 *
	 * @param embeddingModel 嵌入模型，用于文本向量化处理（虽然代码中未直接使用，但可能在VectorStore内部调用）
	 * @param vectorStore 向量存储库，用于存储和检索文本向量数据
	 * @param termsOfServiceDocs 通过@Value注入的类路径资源，指向服务条款文本文件
	 * @return CommandLineRunner 实例，包含初始化向量库和演示搜索的逻辑
	 */
	@Bean
	CommandLineRunner ingestTermOfServiceToVectorStore(EmbeddingModel embeddingModel, VectorStore vectorStore,
	        @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs) {
	
	    return args -> {
	        // 文档注入处理流程：读取文本->分割为token块->写入向量存储库
	        vectorStore.write(new TokenTextSplitter().transform(new TextReader(termsOfServiceDocs).read()));
	
	        // 执行相似性搜索演示：查找与"取消预订"相关的条款并记录结果 doc.getContent() --> doc.getText() [版本调整]
	        vectorStore.similaritySearch("Cancelling Bookings").forEach(doc -> {
	            logger.info("Similar Document: {}", doc.getText());
	        });
	    };
	}

	/**
	 * 创建并配置一个向量存储实例。
	 * <p>
	 * 本方法使用指定的嵌入模型初始化简单向量存储，适用于需要将数据向量化存储和检索的场景。
	 * 
	 * @param embeddingModel 嵌入模型实例，用于将原始数据转换为向量表示
	 *                        该模型应实现文本/数据的特征提取功能
	 * @return 配置完成的向量存储实例，提供基础的向量存储和相似性检索能力
	 *         实际返回类型为SimpleVectorStore，是VectorStore接口的具体实现
	 */
	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		// 初始化简单向量存储并注入嵌入模型依赖
		// new SimpleVectorStore(embeddingModel) --> SimpleVectorStore.builder(embeddingModel).build()
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	/**
	 * 创建并配置一个ChatMemory类型的Spring Bean实例
	 * 
	 * 该方法使用@Bean注解声明，用于在Spring应用上下文中注册一个基于内存实现的聊天记忆组件。
	 * 该组件用于管理聊天会话的上下文信息，保持对话状态和临时数据存储。
	 *
	 * @return InMemoryChatMemory 返回基于内存实现的ChatMemory组件实例，
	 *         适用于需要临时存储和快速访问聊天上下文的场景。该实现不持久化数据，
	 *         当应用重启时存储内容将被清空。
	 */
	@Bean
	public ChatMemory chatMemory() {
		return new InMemoryChatMemory();
	}

	/**
	 * 创建并返回一个 {@link RestClient.Builder} 实例的 Spring Bean。
	 * 该 Bean 的创建由以下条件控制：
	 * <ul>
	 *   <li>通过 {@link Bean @Bean} 注解声明为 Spring 容器管理的 Bean</li>
	 *   <li>通过 {@link ConditionalOnMissingBean @ConditionalOnMissingBean} 确保仅在当前上下文中
	 *       不存在其他 {@link RestClient.Builder} 类型的 Bean 时才会创建</li>
	 * </ul>
	 *
	 * @return 用于构建 {@link RestClient} 的基础建造者对象，该对象已预先配置默认设置，
	 *         可通过返回的 Builder 进行进一步的自定义配置
	 */
	@Bean
	@ConditionalOnMissingBean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

}
