package com.backend.curi.workFlow.controller;

import com.backend.curi.workFlow.controller.dto.WorkFlowForm;
import com.backend.curi.workFlow.repository.entity.WorkFlow;
import com.backend.curi.workFlow.service.WorkFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;


@RequiredArgsConstructor
@RequestMapping("/workFlow")
public class WorkFlowController {
    private WorkFlowService workFlowService;

    @PostMapping("/create")
    public ResponseEntity createWorkFlow(@RequestBody @Valid WorkFlowForm workFlowForm, Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);

    }

    @PostMapping("/getList")
    public ResponseEntity getWorkFlowList (Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("/update")
    public ResponseEntity setWorkFlow(@RequestBody @Valid WorkFlowForm workFlowForm, Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }



}
