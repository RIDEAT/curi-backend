package com.backend.curi.workflow.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.SequenceRepository;
import com.backend.curi.workflow.repository.WorkflowSequenceRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SequenceService {
    private final SequenceRepository sequenceRepository;
    private final WorkflowSequenceRepository workflowSequenceRepository;
    private final WorkspaceService workspaceService;
    private final WorkflowService workflowService;

    public SequenceResponse getSequence(Long sequenceId){
        var sequence = getSequenceEntity(sequenceId);
        return SequenceResponse.of(sequence);
    }

    public List<SequenceResponse> getSequences(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var sequences = sequenceRepository.findAllByWorkspace(workspace);
        return sequences.stream()
                .map(SequenceResponse::of)
                .collect(Collectors.toList());
    }

    public Sequence createSequence(Long workspaceId, SequenceRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        var role = workspaceService.getRoleEntityByIdAndWorkspace(request.getRoleId(), workspace);
        var sequence = Sequence.of(request, role, workspace);
        sequenceRepository.save(sequence);

        return sequence;
    }

    @Transactional
    public void createSequence(Long workspaceId, Long workflowId, SequenceRequest request){
        var sequence = createSequence(workspaceId, request);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var prevSequence = sequenceRepository.findById(request.getPrevSequenceId());

        var checkRelation = workflowSequenceRepository.findByWorkflowAndSequence(workflow, sequence);
        if(checkRelation.isPresent())
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.WORKFLOW_SEQUENCE_ALREADY_EXISTS);

        var workflowSequence = WorkflowSequence.builder()
                .workflow(workflow)
                .sequence(sequence)
                .dayOffset(request.getDayOffset())
                .prevSequence(prevSequence)
                .build();

        workflowSequenceRepository.save(workflowSequence);
    }

    public Sequence modifySequence(Long sequenceId, SequenceRequest request){
        var sequence = getSequenceEntity(sequenceId);
        var workspace = sequence.getWorkspace();
        var role = workspaceService.getRoleEntityByIdAndWorkspace(request.getRoleId(), workspace);
        sequence.modify(request, role);
        return sequence;
    }

    @Transactional
    public void modifySequence(Long workflowId, Long sequenceId, SequenceRequest request){
        var sequence = modifySequence(sequenceId, request);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var workspace = sequence.getWorkspace();
        var role = workspaceService.getRoleEntityByIdAndWorkspace(request.getRoleId(), workspace);
        sequence.modify(request, role);

        // 둘의 연관관계가 없으면?
        var workflowSequence = workflowSequenceRepository.findByWorkflowAndSequence(workflow, sequence);
        var prevSequence = sequenceRepository.findById(request.getPrevSequenceId());
        if(workflowSequence.isEmpty()) {
            var newWorkflowSequence = WorkflowSequence.builder()
                    .workflow(workflow)
                    .sequence(sequence)
                    .dayOffset(request.getDayOffset())
                    .prevSequence(prevSequence)
                    .build();

            workflowSequenceRepository.save(newWorkflowSequence);
        }
        else {
            workflowSequence.get().modify(request, prevSequence);
        }
    }

    public void deleteSequence(Long sequenceId){
        var sequence = getSequenceEntity(sequenceId);
        sequenceRepository.delete(sequence);
    }
    public void deleteWorkflowSequence(Long workflowId, Long sequenceId){
        var sequence = getSequenceEntity(sequenceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var workflowSequence = getWorkflowSequence(workflow, sequence);
        workflowSequenceRepository.delete(workflowSequence);
    }

    public Sequence getSequenceEntity(Long sequenceId){
        return sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS));
    }

    private WorkflowSequence getWorkflowSequence(Workflow workflow, Sequence sequence){
        return workflowSequenceRepository.findByWorkflowAndSequence(workflow, sequence)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_SEQUENCE_NOT_EXISTS));
    }
}
