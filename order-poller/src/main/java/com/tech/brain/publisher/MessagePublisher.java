package com.tech.brain.publisher;

import com.tech.brain.model.ProductEvent;
import com.tech.brain.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class MessagePublisher {

    @Value("${order.poller.topic.name}")
    private String topicName;

    private final JSONUtils jsonUtils;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MessagePublisher(JSONUtils jsonUtils, KafkaTemplate<String, Object> kafkaTemplate) {
        this.jsonUtils = jsonUtils;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ProductEvent message) {
        log.info("Publishing: [{}]", jsonUtils.javaToJSON(message));
        CompletableFuture<SendResult<String, Object>> future = this.kafkaTemplate.send(topicName, message.getEventType(), message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to:{}", message, ex.getMessage());
            }
        });
    }

}
