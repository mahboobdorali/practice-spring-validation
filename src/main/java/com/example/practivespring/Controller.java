package com.example.practivespring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class Controller {
    @GetMapping(value = "/other-class")
    public JsonMessage hateoasInOtherClass() {
        return new JsonMessage("**hello hateoas in other class**");
    }

}
