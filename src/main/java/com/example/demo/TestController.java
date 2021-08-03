package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/version")
    ResponseEntity test(@RequestParam String version) {
        Greeting greeting = new Greeting();
        greeting.setFirstName("Charles");
        greeting.setLastName("Cao");
        greeting.setVersion(version);
        return ResponseEntity.ok().body(greeting);
    }
}
