package com.backend.curi.launched.launchedsequence.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.launchedsequence.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launched.launchedsequence.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.launchedsequence.repository.LaunchedSequenceRepository;
import com.backend.curi.launched.launchedsequence.repository.entity.LaunchedSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LaunchedSequenceService {

    private final LaunchedSequenceRepository launchedSequenceRepository;

    public LaunchedSequenceResponse getLaunchedSequence(Long sequenceId) {
        Optional<LaunchedSequence> launchedSequence = launchedSequenceRepository.findById(sequenceId);
        if (launchedSequence.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS);
        return LaunchedSequenceResponse.of(launchedSequence.get());
    }




    public LaunchedSequenceResponse createLaunchedSequence(LaunchedSequenceRequest createdLaunchedSequence) {
        //Employee employee = memberService.getEmployeeById(createdLaunchedWorkflow.getEmployeeId());
        //Workspace workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        //Workflow workflow = workflowService.getWorkflowById(createdLaunchedWorkflow.getWorkflowId());

        LaunchedSequence newLaunchedSequence = LaunchedSequence.of(createdLaunchedSequence);
        LaunchedSequence savedLaunchedSequence = launchedSequenceRepository.save(newLaunchedSequence);
        return LaunchedSequenceResponse.of(savedLaunchedSequence);
    }

}
