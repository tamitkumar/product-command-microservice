package com.tech.brain.publisher;

import com.tech.brain.model.ProductEvent;
import com.tech.brain.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JSONUtils jsonUtils;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate, JSONUtils jsonUtils) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonUtils = jsonUtils;
    }

    public void publish(ProductEvent message) {
        log.info("Publishing: [{}]", jsonUtils.javaToJSON(message));
        CompletableFuture<SendResult<String, Object>> future = this.kafkaTemplate.send("product-event-topic", message.getProduct().getProductCode(), message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to:{}", message, ex.getMessage());
            }
        });
    }
}
