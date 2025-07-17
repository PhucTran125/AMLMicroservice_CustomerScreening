package com.vpbankhackathon.customer_screening_service.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "sanctions_list")
@Data
public class SanctionsList {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_identification_number")
    private String customerIdentificationNumber;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "reason", length = 200)
    private String reason;
}