package com.devops.devops_accommodation.dto;


import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchAccommodationDTO {
    private String city; //obavezan unos
    private String country; //obavezan unos
    private Integer numGuest; //obavezan unos
    private LocalDate startDate; //obavezan unos
    private LocalDate endDate; //obavezan unos
}
