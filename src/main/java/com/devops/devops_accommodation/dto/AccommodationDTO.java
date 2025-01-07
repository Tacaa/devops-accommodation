package com.devops.devops_accommodation.dto;


import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.model.Accommodation;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDTO {
    private Integer id;
    private String name;
    private AddressDTO address; // DTO for address
    private Set<Benefits> benefits;
    private List<String> photos;
    private Integer minGuests;
    private Integer maxGuests;
    private PriceType priceType;
    private RequestApproval requestApproval;
    private Integer hostId;

    public static AccommodationDTO from(Accommodation accommodation) {
        return AccommodationDTO.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .address(AddressDTO.from(accommodation.getAddress()))
                .benefits(accommodation.getBenefits())
                .photos(accommodation.getPhotos())
                .minGuests(accommodation.getMinGuests())
                .maxGuests(accommodation.getMaxGuests())
                .priceType(accommodation.getPriceType())
                .requestApproval(accommodation.getRequestApproval())
                .hostId(accommodation.getHostId())
                .build();
    }
}