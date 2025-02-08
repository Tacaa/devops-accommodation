package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.model.Address;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Integer id;
    private String street;
    private Integer number;
    private String city;
    private String country;

    public static AddressDTO from(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .number(address.getNumber())
                .city(address.getCity())
                .country(address.getCountry())
                .build();
    }

    public static Address from(AddressDTO addressDTO){
        return Address.builder()
                .street(addressDTO.getStreet())
                .number(addressDTO.getNumber())
                .city(addressDTO.getCity())
                .country(addressDTO.getCountry())
                .build();
    }

}
