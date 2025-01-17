package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.startDate <= :endDate AND r.endDate >= :startDate AND r.status = 'ACCEPTED' AND r.deleted = false AND r.canceled = false")
    boolean existsReservation(@Param("accommodationId") Integer accommodationId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Reservation r WHERE r.accommodation.hostId = :hostId AND r.status = 'PENDING' AND r.deleted = false AND r.canceled = false")
    List<Reservation> getAllPendingReservationRequestsByHost(@Param("hostId") Integer hostId);

    @Query("SELECT r FROM Reservation r WHERE r.id IN :ids")
    List<Reservation> findListOfReservations(@Param("ids") List<Integer> ids);
}
