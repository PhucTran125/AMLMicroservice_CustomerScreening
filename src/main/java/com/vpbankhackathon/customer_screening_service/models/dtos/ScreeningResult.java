package com.vpbankhackathon.customer_screening_service.models.dtos;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Entity
public class ScreeningResult {
//    @Id
    private String transactionId;
    private String customerName;
    private String identificationNumber;
    private RiskLevel riskLevel;
    private Decision decision;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    public enum Decision { APPROVE, REJECT, FLAG_FOR_REVIEW }
}
