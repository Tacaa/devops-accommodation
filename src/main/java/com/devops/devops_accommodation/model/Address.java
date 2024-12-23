package com.devops.devops_accommodation.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String street;

    @Column(name = "number_of_building")
    private Integer number;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;
}
