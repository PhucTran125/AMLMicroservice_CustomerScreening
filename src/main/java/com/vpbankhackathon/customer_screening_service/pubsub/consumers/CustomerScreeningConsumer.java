package com.vpbankhackathon.customer_screening_service.pubsub.consumers;

import com.vpbankhackathon.customer_screening_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.customer_screening_service.models.dtos.ScreeningResult;
import com.vpbankhackathon.customer_screening_service.pubsub.producers.RequestAckProducer;
import com.vpbankhackathon.customer_screening_service.services.CustomerScreening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerScreeningConsumer {

    private final CustomerScreening customerScreening;

    @Autowired
    RequestAckProducer requestAckProducer;

    @Autowired
    public CustomerScreeningConsumer(CustomerScreening customerScreening) {
        this.customerScreening = customerScreening;
    }

    @KafkaListener(topics = "customer-screening-requests", groupId = "customer-screening-group")
    public void listenCustomerScreeningRequestMsg(CustomerScreeningRequest request) {
        System.out.println("Received customer screening request: " + request.getCustomerId());
        requestAckProducer.sendMessage(request);
        ScreeningResult screeningResult = customerScreening.verifyCustomer(request);
        customerScreening.handleScreeningResult(screeningResult, request.getRequestId());
    }
}
