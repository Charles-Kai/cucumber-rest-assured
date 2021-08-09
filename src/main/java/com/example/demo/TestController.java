package com.example.demo;

import com.example.demo.raml.RequestContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/version")
    ResponseEntity<Greeting> test(RequestContext<Void> requestContext) {
        String version = requestContext.getParamter("version").orElseThrow(IllegalArgumentException::new);
        Greeting greeting = new Greeting();
        greeting.setFirstName("Charles");
        greeting.setLastName("Cao");
        greeting.setVersion(version);
        return ResponseEntity.ok().body(greeting);
    }
}
