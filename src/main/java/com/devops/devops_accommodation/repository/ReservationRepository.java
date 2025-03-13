package com.devops.devops_accommodation.repository;

import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
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

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.guestId = :guestId")
    boolean didGuestHadReservationInAccommodation(@Param("accommodationId") Integer accommodationId, @Param("guestId") Integer guestId);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.accommodation.hostId = :hostId AND r.guestId = :guestId")
    boolean didGuestHadReservationInHostAccommodation(@Param("hostId") Integer hostId, @Param("guestId") Integer guestId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE r.accommodation = :accommodation " +
            "AND r.startDate <= :endDate AND r.endDate >= :startDate AND r.deleted = false AND r.canceled = false")
    boolean existsByAccommodationAndDateRange(
            @Param("accommodation") Accommodation accommodation,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE r.guestId = :guestId " +
            "AND (r.startDate >= :today OR (r.startDate < :today AND r.endDate >= :today)) AND r.deleted = false AND r.canceled = false")
    boolean isGuestHavingReservationAtMoment(@Param("guestId") Integer guestId,
                                             @Param("today") LocalDate today);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE r.accommodation.id = :accommodationId " +
            "AND (r.startDate >= :today OR (r.startDate < :today AND r.endDate >= :today)) AND r.deleted = false AND r.canceled = false")
    boolean isAccommodationHavingReservationAtMoment(@Param("accommodationId") Integer accommodationId,
                                             @Param("today") LocalDate today);
}

