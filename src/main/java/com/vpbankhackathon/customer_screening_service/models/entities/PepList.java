package com.vpbankhackathon.customer_screening_service.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "pep_list")
@Data
public class PepList {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_identification_number")
    private String customerIdentificationNumber;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "relation", length = 50)
    private String relation;
}