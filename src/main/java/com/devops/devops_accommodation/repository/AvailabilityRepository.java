package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Availability a WHERE a.accommodation = :accommodation " +
            "AND a.startDate <= :endDate AND a.endDate >= :startDate " +
            "AND a.available = true AND a.deleted = false")
    boolean existsAvailabilityByAccommodationAndDateRange(
            @Param("accommodation") Accommodation accommodation,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Availability a WHERE a.id != :availabilityId AND a.accommodation = :accommodation " +
            "AND a.startDate <= :endDate AND a.endDate >= :startDate " +
            "AND a.available = true AND a.deleted = false")
    boolean existsAvailabilityByAvailabilityIdAndDateRange(
            @Param("availabilityId") Integer availabilityId,
            @Param("accommodation") Accommodation accommodation,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
  
   List<Availability> findByAccommodationId(Integer accommodationId);

    @Query("SELECT DISTINCT av FROM Availability av WHERE av.available = true AND :startDate <= av.endDate AND :endDate >= av.startDate AND av.accommodation.id = :accommodationId")
    List<Availability> findAvailabilitiesForReservation(
            @Param("accommodationId") Integer accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT av FROM Availability av WHERE av.available = false AND :startDate = av.startDate AND :endDate = av.endDate AND av.accommodation.id = :accommodationId")
    Availability findReservedAvailabilityToCancel(
            @Param("accommodationId") Integer accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

