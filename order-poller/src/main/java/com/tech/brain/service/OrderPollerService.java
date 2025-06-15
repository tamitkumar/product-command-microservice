package com.tech.brain.service;

import com.tech.brain.entity.OutboxEntity;
import com.tech.brain.exception.ErrorCode;
import com.tech.brain.exception.ErrorSeverity;
import com.tech.brain.exception.PollerException;
import com.tech.brain.model.ProductEvent;
import com.tech.brain.publisher.MessagePublisher;
import com.tech.brain.repository.OutboxRepository;
import com.tech.brain.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class OrderPollerService {

    private final OutboxRepository repository;
    private final MessagePublisher messagePublisher;
    private final JSONUtils jsonUtils;

    public OrderPollerService(OutboxRepository repository, MessagePublisher messagePublisher, JSONUtils jsonUtils) {
        this.repository = repository;
        this.messagePublisher = messagePublisher;
        this.jsonUtils = jsonUtils;
    }

    @Scheduled(fixedRate = 60000) // 1 Minute
    public void pollOutboxMessagesAndPublish() {

        //1. fetch unprocessed record
        List<OutboxEntity> unprocessedRecords = repository.findByProcessedFalse();

        log.info("unprocessed record count : {}", unprocessedRecords.size());

        //2. publish record to kafka/queue

        unprocessedRecords.forEach(outbox -> {
            try {
                ProductEvent product = jsonUtils.jsonToJava(outbox.getPayload(), ProductEvent.class);
                messagePublisher.publish(product);
                //update the message status to processed=true to avoid duplicate message processing
                outbox.setProcessed(true);
                repository.save(outbox);

            } catch (Exception ig) {
                log.error("error processing outbox", ig);
                throw new PollerException(ErrorCode.HTTP_CODE_500.getErrorCode(), ErrorSeverity.ERROR, ErrorCode.HTTP_CODE_500.getErrorMessage());
            }
        });


    }
}
