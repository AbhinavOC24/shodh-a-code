package com.shodhai.shodhacode.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RootController {
    @GetMapping("/")
    public String ping() {
        return "✅ Shodh-a-Code backend running";
    }
}
