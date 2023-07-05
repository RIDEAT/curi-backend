package com.backend.curi.workflow.controller;

import com.backend.curi.workflow.controller.dto.WorkflowForm;
import com.backend.curi.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;


@RequiredArgsConstructor
@RequestMapping("/workflow")
public class WorkflowController {
    private WorkflowService workflowService;

    @PostMapping("/create")
    public ResponseEntity createWorkflow(@RequestBody @Valid WorkflowForm workflowForm, Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);

    }

    @PostMapping("/list")
    public ResponseEntity getWorkflowList (Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("/update")
    public ResponseEntity setWorkflow(@RequestBody @Valid WorkflowForm workflowForm, Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }



}
