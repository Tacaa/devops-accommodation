package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.startDate <= :endDate AND r.endDate >= :startDate AND r.status = 'ACCEPTED'")
    boolean existsReservation(@Param("accommodationId") Integer accommodationId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
