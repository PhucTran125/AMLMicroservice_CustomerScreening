package com.vpbankhackathon.customer_screening_service.repositories;

import com.vpbankhackathon.customer_screening_service.models.entities.SanctionsList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionListRepository extends JpaRepository<SanctionsList, String> {
}
