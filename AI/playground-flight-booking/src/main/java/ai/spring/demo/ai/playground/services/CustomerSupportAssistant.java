/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.spring.demo.ai.playground.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * * @author Christian Tzolov
 */
@Service
public class CustomerSupportAssistant {

    // 定义一个私有的final变量chatClient，用于存储ChatClient实例
    private final ChatClient chatClient;

    // 构造函数，接受三个参数：modelBuilder, vectorStore, chatMemory
    public CustomerSupportAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {

        // 使用modelBuilder构建ChatClient实例
        this.chatClient = modelBuilder
                // 设置默认的系统提示，包含角色定义和操作指南
                .defaultSystem("""
                           您是“AIFuture”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                           您正在通过在线聊天系统与客户互动。
                           您能够支持机票预定及已有机票的预订详情查询、机票日期改签、机票预订取消等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
                           在提供有关机票预订详情查询、机票日期改签、机票预订取消等操作之前，您必须始终从用户处获取以下信息：预订号、客户姓名。
                           在询问用户之前，请检查消息历史记录以获取预订号、客户姓名等信息，尽量避免重复询问给用户造成困扰。
                           在更改预订之前，您必须确保条款允许这样做。
                           如果更改需要收费，您必须在继续之前征得用户同意。
                           在更改或退订之前，请先获取预订信息并且⽤户确定信息。
                           使用提供的功能获取预订详细信息、更改预订和取消预订。
                           如果需要，您可以调用相应函数辅助完成。
                           请讲中文。
                           今天的日期是 {current_date}.
                        """)
                // 设置默认的顾问，包括聊天记忆顾问、问答顾问和日志顾问
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory), // Chat Memory
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(4).similarityThresholdAll().build()), // RAG
                        new SimpleLoggerAdvisor()
                )

                // 设置默认的函数调用，包括获取预订详情、更改预订和取消预订
                .defaultFunctions("getBookingDetails", "changeBooking", "cancelBooking", "booking") // FUNCTION CALLING
                .build();
    }

    /**
     * 定义一个方法chat，接受两个参数：chatId和userMessageContent，返回一个Flux<String>对象
     */
    public Flux<String> chat(String chatId, String userMessageContent) {

        // 使用chatClient的prompt方法创建一个聊天提示
        return this.chatClient.prompt()
                // 设置系统参数，包括当前日期
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                // 设置用户消息内容
                .user(userMessageContent)
                // 设置顾问参数，包括聊天记忆ID和检索大小
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                // 获取聊天流并返回内容
                .stream()
                .content();
    }

}