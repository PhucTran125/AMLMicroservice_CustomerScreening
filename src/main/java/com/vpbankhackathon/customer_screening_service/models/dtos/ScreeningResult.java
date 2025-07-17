package com.vpbankhackathon.customer_screening_service.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreeningResult {
    private Long customerId;
    private String customerName;
    private String identificationNumber;
    private RiskLevel riskLevel;
    private Decision decision;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    public enum Decision { APPROVE, REJECT, FLAG_FOR_REVIEW }
}
