package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Integer> {
}
