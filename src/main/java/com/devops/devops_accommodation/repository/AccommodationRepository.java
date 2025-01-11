package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Integer> {

    
    @Query("SELECT DISTINCT a FROM Accommodation a JOIN a.address addr JOIN a.availabilities av WHERE addr.city = :city AND addr.country = :country AND a.minGuests <= :numGuest AND a.maxGuests >= :numGuest AND av.available = true AND av.available = true AND :startDate <= av.endDate AND :endDate >= av.startDate")
    List<Accommodation> searchAccommodations(
            @Param("city") String city,
            @Param("country") String country,
            @Param("numGuest") Integer numGuest,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
