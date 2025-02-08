package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.model.Accommodation;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAccommodationResultDTO {
    private AccommodationDTO accommodationDTO;
    private Double unitPrice;
    private Double totalPrice;

    public static SearchAccommodationResultDTO from(Accommodation accommodation, double unitPrice, double totalPrice) {
        return SearchAccommodationResultDTO.builder()
                .accommodationDTO(AccommodationDTO.from(accommodation))
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }
}
