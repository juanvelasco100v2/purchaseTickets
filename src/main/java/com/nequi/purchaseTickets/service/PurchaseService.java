package com.nequi.purchaseTickets.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);
    private final SqsAsyncClient sqsAsyncClient;

    @Value("${aws.sqs.queue.url:http://localstack:4566/000000000000/manage-order-queue}")
    private String queueUrl;

    public PurchaseService(SqsAsyncClient sqsAsyncClient) {
        this.sqsAsyncClient = sqsAsyncClient;
    }

    public Mono<Void> initiatePurchaseFinalization(String orderId, String status) {
        logger.info("Initiating purchase finalization for order: {} with status: {}", orderId, status);

        String messageBody = String.format("{\"orderId\":\"%s\",\"status\":\"%s\"}", orderId, status);

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        return Mono.fromFuture(() -> sqsAsyncClient.sendMessage(sendMsgRequest))
                .doOnSuccess(response -> logger.info("Message sent to SQS. MessageId: {}", response.messageId()))
                .doOnError(e -> logger.error("Error sending message to SQS", e))
                .then();
    }
}