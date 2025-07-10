package com.vpbankhackathon.customer_screening_service.services;

import com.vpbankhackathon.customer_screening_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.customer_screening_service.models.dtos.ScreeningResult;
import com.vpbankhackathon.customer_screening_service.pubsub.producers.AlertProducer;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerScreening {

    private final Map<String, String> sensitiveList = new HashMap<>();

    @Autowired
    public CustomerScreening() {
//        this.alertProducer = alertProducer;
        // Initialize the sensitive list with some dummy data
        sensitiveList.put("John Doe", "Sensitive");
        sensitiveList.put("123456789", "Sensitive");
        // Add more entries as needed
    }

    public ScreeningResult verifyCustomer(CustomerScreeningRequest request) {
        if (request == null || request.getCustomerName() == null || request.getIdentificationNumber() == null) {
            // throw new ScreeningException("Invalid customer data");
            // TODO: Handle the exception properly
        }

        assert request != null;
        boolean isMatch = matchAgainstSensitiveList(request.getCustomerName(), request.getIdentificationNumber());
        if (isMatch) {
            raiseAlert(request);
        }
        System.out.println(getScreeningResult(request, isMatch).getDecision());
        return getScreeningResult(request, isMatch);
    }

    private static ScreeningResult getScreeningResult(CustomerScreeningRequest request, boolean isMatch) {
        ScreeningResult result = new ScreeningResult();
        result.setTransactionId(request.getTransactionId());
        result.setCustomerName(request.getCustomerName());
        result.setIdentificationNumber(request.getIdentificationNumber());
        if (isMatch) {
            result.setRiskLevel(ScreeningResult.RiskLevel.HIGH);
            result.setDecision(ScreeningResult.Decision.REJECT);
        } else {
            result.setRiskLevel(ScreeningResult.RiskLevel.LOW);
            result.setDecision(ScreeningResult.Decision.APPROVE);
        }
        return result;
    }

    private boolean matchAgainstSensitiveList(String customerName, String identificationNumber) {
        // Logic to match customer data against a sensitive list
        return sensitiveList.containsKey(customerName) || sensitiveList.containsKey(identificationNumber);
    }

    private void raiseAlert(@NotNull CustomerScreeningRequest request) {
        // Logic to raise an alert for suspicious activity
        // This could involve logging the incident, notifying authorities, etc.
//        alertProducer.sendMessage("Alert: Suspicious activity detected for customer: " + request.getCustomerName() +
//                " with ID: " + request.getIdentificationNumber());
        System.out.println("Alert raised for customer: " + request.getCustomerName() + " with ID: "
                + request.getIdentificationNumber());
    }
}
