package com.devops.devops_accommodation.model;

import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodations")
@Builder
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false, referencedColumnName = "id")
    private Address address;

    @ElementCollection(targetClass = Benefits.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "accommodation_benefits",
            joinColumns = @JoinColumn(name = "accommodation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit")
    private Set<Benefits> benefits = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "accommodation_photos", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @Column(name = "min_guests",  nullable = false)
    private Integer minGuests;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", nullable = false)
    private PriceType priceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_approval", nullable = false)
    private RequestApproval requestApproval;

    @Column(name = "host_id", nullable = false)
    private Integer hostId;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Availability> availabilities = new ArrayList<>();

    public void addAvailability(Availability availability) {
        availabilities.add(availability);
        availability.setAccommodation(this);
    }

    public void removeAvailability(Availability availability) {
        availabilities.remove(availability);
        availability.setAccommodation(null);
    }

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setAccommodation(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setAccommodation(null);
    }

}
