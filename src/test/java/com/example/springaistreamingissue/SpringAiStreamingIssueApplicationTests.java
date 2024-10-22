package com.example.springaistreamingissue;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.azure.core.implementation.serializer.DefaultJsonSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiStreamingIssueApplicationTests {

    @Autowired
    private ChatModel chatModel;

    @Test
    void contextLoads() {
        var logger = (Logger) LoggerFactory.getLogger(
            DefaultJsonSerializer.class);

        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        var chatClient = ChatClient.builder(chatModel).build();
        var flux = chatClient.prompt("Can you write a long message? Lorum ipsum for instance.")
            .stream().content();

        flux.blockLast();

        assertThat(listAppender.list).extracting(ILoggingEvent::getFormattedMessage).anyMatch(
            s -> s.contains("com.azure.json.implementation.jackson.core.io.JsonEOFException"));
    }

}
