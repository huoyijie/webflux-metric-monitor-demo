package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by huoyijie on 18/11/18.
 */
@RestController
public class HelloController {

    @GetMapping("/hello2")
    public String handle() {
        return "Hello WebFlux";
    }
}
