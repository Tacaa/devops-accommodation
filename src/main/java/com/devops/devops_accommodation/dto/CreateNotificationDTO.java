package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.enumeration.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationDTO {
    private Integer receiverId;
    private Integer senderId;
    private String title;
    private String content;
    private NotificationType notificationType;
}