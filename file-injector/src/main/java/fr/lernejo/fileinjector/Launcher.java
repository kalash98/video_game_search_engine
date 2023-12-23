package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class Launcher {
        private static final String queueName = "chat_messages";
        public static void main(String[] args){
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Launcher.class);
            RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
            String jsonFilePath = args[0];
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(new File(jsonFilePath));
                if (jsonNode.isArray()) {
                    Iterator<JsonNode> elements = jsonNode.elements();
                    while (elements.hasNext()) {
                        JsonNode messageNode = elements.next();
                        Map<String, Object> message = objectMapper.convertValue(messageNode, Map.class);
                        Message rabbitMessage = MessageBuilder.withBody(objectMapper.writeValueAsBytes(message)).setContentType("application/json").setMessageId(UUID.randomUUID().toString()).build();
                        rabbitTemplate.convertAndSend("game_info", rabbitMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
