package com.vpbankhackathon.customer_screening_service.repositories;

import com.vpbankhackathon.customer_screening_service.models.entities.PepList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PepListRepository extends JpaRepository<PepList, String> {
}
