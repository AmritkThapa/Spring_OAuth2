package com.amrit.SpringOauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String test() {
        return "Test endpoint is working!";
    }
    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }
}
