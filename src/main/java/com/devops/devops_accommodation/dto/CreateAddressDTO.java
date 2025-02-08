package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.model.Address;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAddressDTO {
    private String street;
    private Integer number;
    private String city;
    private String country;

    public static Address from(CreateAddressDTO addressDTO){
        return Address.builder()
                .street(addressDTO.getStreet())
                .number(addressDTO.getNumber())
                .city(addressDTO.getCity())
                .country(addressDTO.getCountry())
                .build();
    }
}
