package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface AccommodationRepository extends JpaRepository<Accommodation, Integer> {
   
  @Query("SELECT COUNT(a) > 0 FROM Accommodation a JOIN a.availabilities av WHERE a.id = :accommodationId AND av.available = true AND :startDate <= av.endDate AND :endDate >= av.startDate")
    boolean checkAccommodationsAvailability(
            @Param("accommodationId") Integer accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
  @Query("SELECT DISTINCT a FROM Accommodation a JOIN a.address addr JOIN a.availabilities av WHERE a.deleted = false AND addr.city = :city AND addr.country = :country AND a.minGuests <= :numGuest AND a.maxGuests >= :numGuest AND av.available = true AND :startDate <= av.endDate AND :endDate >= av.startDate AND av.deleted = false")
  List<Accommodation> searchAccommodations(
            @Param("city") String city,
            @Param("country") String country,
            @Param("numGuest") Integer numGuest,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT DISTINCT a FROM Accommodation a JOIN a.address addr JOIN a.reservations r WHERE a.deleted = false AND addr.city = :city AND addr.country = :country AND a.minGuests <= :numGuest AND a.maxGuests >= :numGuest AND :startDate <= r.endDate AND :endDate >= r.startDate AND r.deleted = false AND r.canceled = false")
    List<Accommodation> searchReservedAccommodations(
            @Param("city") String city,
            @Param("country") String country,
            @Param("numGuest") Integer numGuest,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT a FROM Accommodation a WHERE a.hostId = :hostId AND a.deleted = false")
    List<Accommodation> findAllByHostId(@Param("hostId") Integer hostId);
}
