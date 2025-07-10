package com.vpbankhackathon.customer_screening_service.pubsub.producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AlertProducer {
//    @Autowired
//    KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    // public void sendMessage(String msg) {
    // CompletableFuture<SendResult<String, String>> future =
    // kafkaTemplate.send(topicName, msg);
    // future.whenComplete((result, ex) -> {
    // if (ex == null) {
    // System.out.println("Sent message=[" + msg +
    // "] with offset=[" + result.getRecordMetadata().offset() + "]");
    // } else {
    // System.out.println("Unable to send message=[" +
    // msg + "] due to : " + ex.getMessage());
    // }
    // });
    // }
}
