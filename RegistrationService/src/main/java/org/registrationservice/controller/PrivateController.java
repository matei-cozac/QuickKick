package org.registrationservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrivateController {

    @GetMapping("/secured")
    public String secured() {
        return "Hello, Secured!";
    }
}
