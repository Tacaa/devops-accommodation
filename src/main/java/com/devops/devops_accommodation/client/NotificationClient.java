package com.devops.devops_accommodation.client;

import com.devops.devops_accommodation.dto.CreateNotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "notifications", url = "${notification.service.url}")
public interface NotificationClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/notifications/save")
    Boolean sendNotification(@RequestBody CreateNotificationDTO createNotificationDTO);
}
