package com.devops.devops_accommodation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationNotificationData {
    private String accommodationName;
    private Integer accommodationHostId;
}
