package com.backend.curi.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {
    @GetMapping("/health")
    public ResponseEntity<Void> checkHealth(){
        return ResponseEntity.ok().build();
    }
}
