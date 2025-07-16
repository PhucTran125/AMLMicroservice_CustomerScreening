package com.vpbankhackathon.customer_screening_service.models.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AMLRequest {
    public enum RequestStatus {
        PENDING("PENDING"),
        SENT("SENT"),
        ACKED("ACKED");

        RequestStatus(String pending) {}
    }

    private UUID id = UUID.randomUUID();
    private Long timestamp = Instant.now().toEpochMilli();
    private RequestStatus status;
    private String data;
}