package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AccommodationRepository extends JpaRepository<Accommodation, Integer> {

    @Query("SELECT COUNT(a) > 0 FROM Accommodation a JOIN a.availabilities av WHERE a.id = :accommodationId AND av.available = true AND :startDate <= av.endDate AND :endDate >= av.startDate")
    boolean checkAccommodationsAvailability(
            @Param("accommodationId") Integer accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
