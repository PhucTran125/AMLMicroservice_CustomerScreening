package com.vpbankhackathon.customer_screening_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vpbankhackathon.customer_screening_service.models.dtos.AMLRequest;
import com.vpbankhackathon.customer_screening_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.customer_screening_service.models.dtos.CustomerScreeningResult;
import com.vpbankhackathon.customer_screening_service.models.dtos.ScreeningResult;
import com.vpbankhackathon.customer_screening_service.models.entities.PepList;
import com.vpbankhackathon.customer_screening_service.models.entities.SanctionsList;
import com.vpbankhackathon.customer_screening_service.pubsub.producers.AlertProducer;
import com.vpbankhackathon.customer_screening_service.pubsub.producers.CustomerScreeningResultProducer;
import com.vpbankhackathon.customer_screening_service.repositories.PepListRepository;
import com.vpbankhackathon.customer_screening_service.repositories.SanctionListRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerScreening {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    public CustomerScreening() {}

    @Autowired
    private CustomerScreeningResultProducer customerScreeningResultProducer;

    @Autowired
    PepListRepository pepListRepository;

    @Autowired
    SanctionListRepository sanctionListRepository;

    public ScreeningResult verifyCustomer(CustomerScreeningRequest request) {
        if (request == null || request.getCustomerName() == null || request.getCustomerId() == null) {
            // throw new ScreeningException("Invalid customer data");
            // TODO: Handle the exception properly
        }

        assert request != null;
        boolean isMatchPepList = matchAgainstPepList(request.getCustomerIdentificationNumber());
        boolean isMatchSanctionList = matchAgainstSanctionList(request.getCustomerIdentificationNumber());
        if (isMatchSanctionList || isMatchPepList) {
            raiseAlert(request);
        }
        return getScreeningResult(request, isMatchPepList, isMatchSanctionList);
    }

    private static ScreeningResult getScreeningResult(
        CustomerScreeningRequest request,
        boolean isMatchPepList,
        boolean isMatchSanctionList
    ) {
        ScreeningResult result = new ScreeningResult();
        result.setCustomerId(request.getCustomerId());
        result.setCustomerName(request.getCustomerName());
        result.setIdentificationNumber(request.getCustomerIdentificationNumber());
        if (isMatchPepList || isMatchSanctionList) {
            result.setDecision(ScreeningResult.Decision.REJECT);
            if (isMatchPepList && isMatchSanctionList) {
                result.setReason("Matched both PEP and Sanction lists");
            } else if (isMatchPepList) {
                result.setReason("Matched PEP list");
            } else {
                result.setReason("Matched Sanction list");
            }
            result.setRiskLevel(ScreeningResult.RiskLevel.HIGH);
        } else {
            result.setRiskLevel(ScreeningResult.RiskLevel.LOW);
            result.setDecision(ScreeningResult.Decision.APPROVE);
        }
        return result;
    }

    public void handleScreeningResult(ScreeningResult screeningResult, String requestId) {
        CustomerScreeningResult cSResult = new CustomerScreeningResult();
        cSResult.setCustomerId(screeningResult.getCustomerId());
        cSResult.setRequestId(requestId);
        switch (screeningResult.getDecision()) {
            case APPROVE -> cSResult.setStatus(CustomerScreeningResult.Status.CLEAR);
            case REJECT -> cSResult.setStatus(CustomerScreeningResult.Status.SUSPENDED);
            default -> cSResult.setStatus(CustomerScreeningResult.Status.SUSPICIOUS);
        }
        cSResult.setReason(screeningResult.getReason());
        customerScreeningResultProducer.sendMessage(cSResult);
    }

    private boolean matchAgainstSanctionList(String identificationNumber) {
        // Check against Sanctions list
        List<SanctionsList> sanctionsList = sanctionListRepository.findAll();
        return sanctionsList.stream()
            .anyMatch(pep -> pep.getCustomerIdentificationNumber().equals(identificationNumber));
    }

    private boolean matchAgainstPepList(String identificationNumber) {
        // Check against PEP list
        List<PepList> pepList = pepListRepository.findAll();
        return pepList.stream()
                .anyMatch(pep -> pep.getCustomerIdentificationNumber().equals(identificationNumber));
    }

    private void raiseAlert(@NotNull CustomerScreeningRequest request) {
        // Logic to raise an alert for suspicious activity
        // This could involve logging the incident, notifying authorities, etc.
        // alertProducer.sendMessage("Alert: Suspicious activity detected for customer:
        // " + request.getCustomerName() +
        // " with ID: " + request.getIdentificationNumber());
        System.out.println("Alert raised for AML Request ID: " + request.getRequestId() +
                " - Customer: " + request.getCustomerName() +
                " with ID: " + request.getCustomerId());
    }
}
