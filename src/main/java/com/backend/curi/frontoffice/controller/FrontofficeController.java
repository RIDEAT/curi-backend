package com.backend.curi.frontoffice.controller;

import com.backend.curi.frontoffice.controller.dto.FrontofficeResponse;
import com.backend.curi.frontoffice.service.FrontofficeService;
import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/frontoffices")
@RequiredArgsConstructor
public class FrontofficeController {

    private final FrontofficeService frontofficeService;

    @GetMapping("/{frontofficeId}")
    public ResponseEntity<FrontofficeResponse> getLaunchedsequence(@PathVariable UUID frontofficeId){
        FrontofficeResponse frontofficeResponse =  frontofficeService.getFrontoffice(frontofficeId);
        return ResponseEntity.ok(frontofficeResponse);
    }



}
