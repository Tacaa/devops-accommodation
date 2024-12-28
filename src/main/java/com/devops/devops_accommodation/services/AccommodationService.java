package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    public Accommodation create(CreateAccommodationDTO createAccommodationDTO) {
        if(createAccommodationDTO.getName() == null
                || createAccommodationDTO.getBenefits() == null
                || createAccommodationDTO.getMinGuests() == null
                || createAccommodationDTO.getMaxGuests() == null
                || createAccommodationDTO.getPriceType() == null
                || createAccommodationDTO.getRequestApproval() == null
                || createAccommodationDTO.getHostId() == null
                ||  createAccommodationDTO.getPhotos() == null
                || createAccommodationDTO.getAddress() == null){
            throw new AttributeNullException("Given accommodation attribute is null");
        }

        if(createAccommodationDTO.getAddress().getStreet() == null
            || createAccommodationDTO.getAddress().getStreet() == null
                || createAccommodationDTO.getAddress().getStreet() == null
                || createAccommodationDTO.getAddress().getStreet() == null){
            throw new AttributeNullException("Given address attribute is null");
        }

        Address address = CreateAddressDTO.from(createAccommodationDTO.getAddress());

        Accommodation accommodation = Accommodation.builder()
                .name(createAccommodationDTO.getName())
                .address(address)
                .benefits(createAccommodationDTO.getBenefits())
                .photos(createAccommodationDTO.getPhotos())
                .minGuests(createAccommodationDTO.getMinGuests())
                .maxGuests(createAccommodationDTO.getMaxGuests())
                .priceType(createAccommodationDTO.getPriceType())
                .requestApproval(createAccommodationDTO.getRequestApproval())
                .hostId(createAccommodationDTO.getHostId())
                .build();

        return accommodationRepository.save(accommodation);
    }

}
