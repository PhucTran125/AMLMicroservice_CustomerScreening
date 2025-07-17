package com.vpbankhackathon.customer_screening_service.models.dtos;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomerScreeningRequest {
    private Long customerId;
    private String customerName;
    private String customerIdentificationNumber;
    private String dob;
    private String nationality;
    private String residentialAddress;
    private String requestId;
}
