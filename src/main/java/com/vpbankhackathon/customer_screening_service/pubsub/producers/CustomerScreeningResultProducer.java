package com.vpbankhackathon.customer_screening_service.pubsub.producers;

import com.vpbankhackathon.customer_screening_service.models.dtos.CustomerScreeningResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CustomerScreeningResultProducer {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topic.customer-screening-result}")
    private String topicName;

    public void sendMessage(CustomerScreeningResult cSResult) {
        try {
            System.out.println("Attempting to send customer screening result to topic: " + topicName);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                topicName,
                String.valueOf(cSResult.getCustomerId()),
                cSResult
            );
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message for account=[" + cSResult.getCustomerId() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send message for account=[" +
                        cSResult.getCustomerId() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage (Customer Screening Result): " + e.getMessage());
            throw new RuntimeException("Failed to send customer screening result to Kafka", e);
        }
    }
}
