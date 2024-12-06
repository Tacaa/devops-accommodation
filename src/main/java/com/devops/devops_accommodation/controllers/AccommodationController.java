package com.devops.devops_accommodation.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/accommodation")
public class AccommodationController  {

    @GetMapping("/")
    public String home() {
    return "Hello, World!";
}
}
