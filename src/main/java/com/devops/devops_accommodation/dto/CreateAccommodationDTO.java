package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccommodationDTO {
    private String name;
    private CreateAddressDTO address; // DTO for address
    private Set<Benefits> benefits;
    private List<String> photos;
    private Integer minGuests;
    private Integer maxGuests;
    private PriceType priceType;
    private RequestApproval requestApproval;
    private Integer hostId;
}
