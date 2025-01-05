package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE r.accommodation = :accommodation " +
            "AND r.startDate <= :endDate AND r.endDate >= :startDate")
    boolean existsByAccommodationAndDateRange(
            @Param("accommodation") Accommodation accommodation,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
