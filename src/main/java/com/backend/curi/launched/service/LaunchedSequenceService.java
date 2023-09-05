package com.backend.curi.launched.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.controller.dto.*;
import com.backend.curi.launched.repository.LaunchedSequenceRepository;
import com.backend.curi.launched.repository.LaunchedWorkflowRepository;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchedSequenceService {

    private final LaunchedSequenceRepository launchedSequenceRepository;
    private final LaunchedWorkflowRepository launchedWorkflowRepository;

    public LaunchedSequenceResponse getLaunchedSequence(Long sequenceId) {
        Optional<LaunchedSequence> launchedSequence = launchedSequenceRepository.findById(sequenceId);
        if (launchedSequence.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS);
        return LaunchedSequenceResponse.of(launchedSequence.get());
    }

    public LaunchedSequence getLaunchedSequenceEntity(Long sequenceId) {
        return launchedSequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS));
    }

    public List<LaunchedSequence> getLaunchedSequenceList(Long workspaceId, LaunchedStatus status){
        List<LaunchedSequence> launchedSequenceListInProgress = launchedSequenceRepository.findAllByStatusAndWorkspaceId(status, workspaceId);
        return launchedSequenceListInProgress.stream().filter(launchedSequence -> launchedSequence.getApplyDate().plusWeeks(1).isBefore(LocalDate.now())).collect(Collectors.toList());
    }


    public LaunchedSequenceResponse createLaunchedSequence(LaunchedSequenceRequest createdLaunchedSequence) {
        //Employee employee = memberService.getEmployeeById(createdLaunchedWorkflow.getEmployeeId());
        //Workspace workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        //Workflow workflow = workflowService.getWorkflowById(createdLaunchedWorkflow.getWorkflowId());

        LaunchedSequence newLaunchedSequence = LaunchedSequence.of(createdLaunchedSequence);
        LaunchedSequence savedLaunchedSequence = launchedSequenceRepository.save(newLaunchedSequence);
        return LaunchedSequenceResponse.of(savedLaunchedSequence);
    }


    @Transactional
    public LaunchedSequenceResponse updateLaunchedSeqeunce(LaunchedSequenceRequest launchedSequenceRequest, Long launchedSequenceId){
        LaunchedSequence launchedSequence = getLaunchedSequenceEntity(launchedSequenceId);
        launchedSequence.modify(launchedSequenceRequest);
        return LaunchedSequenceResponse.of(launchedSequence);
    }

    @Transactional
    public LaunchedSequenceResponse updateLaunchedSequence(LaunchedSequenceUpdateRequest request, Long launchedSequenceId) {
        var launchedSequence = getLaunchedSequenceEntity(launchedSequenceId);
        if(request.getStatus() != null)
            launchedSequence.setStatus(request.getStatus());
        if(request.getApplyDate() != null)
            launchedSequence.setApplyDate(request.getApplyDate());
        if(request.getCheckSatisfaction() != null)
            launchedSequence.setCheckSatisfaction(request.getCheckSatisfaction());

        return LaunchedSequenceResponse.of(launchedSequence);
    }

    public void deleteLaunchedSequence(Long launchedSequenceId){
        LaunchedSequence launchedSequence = getLaunchedSequenceEntity(launchedSequenceId);
        launchedSequenceRepository.delete(launchedSequence);
    }

    public void saveLaunchedSequence (LaunchedSequence launchedSequence){
        launchedSequenceRepository.save(launchedSequence);
    }

    public void completeLaunchedSequence(LaunchedSequence launchedSequence) {
        launchedSequence.setStatus(LaunchedStatus.COMPLETED);
        checkIfAllSequencesCompleted(launchedSequence);
        launchedSequenceRepository.save(launchedSequence);
    }

    private void checkIfAllSequencesCompleted(LaunchedSequence launchedSequence) {
        LaunchedWorkflow launchedWorkflow = launchedSequence.getLauchedWorkflow();
        if (launchedWorkflow.getLaunchedSequences().stream().allMatch(launchedSequence1 -> launchedSequence1.getStatus().equals(LaunchedStatus.COMPLETED))) {
            launchedWorkflow.setStatus(LaunchedStatus.COMPLETED);
            launchedWorkflowRepository.save(launchedWorkflow);
        }
    }
}
