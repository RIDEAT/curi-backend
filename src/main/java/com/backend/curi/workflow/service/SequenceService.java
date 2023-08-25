package com.backend.curi.workflow.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.SequenceUpdateRequest;
import com.backend.curi.workflow.repository.SequenceRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.service.RoleService;
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
    private final WorkspaceService workspaceService;
    private final RoleService roleService;
    private final WorkflowService workflowService;

    public SequenceResponse getSequence(Long sequenceId) {
        var sequence = getSequenceEntity(sequenceId);
        return SequenceResponse.of(sequence);
    }

    public Sequence createSequence(Long workspaceId, Long workflowId, SequenceRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var role = roleService.getRoleEntity(request.getRoleId());
        var sequence = Sequence.of(request, role, workspace, workflow);
        sequenceRepository.save(sequence);

        return sequence;
    }

    public Sequence modifySequence(Long sequenceId, SequenceRequest request) {
        var sequence = getSequenceEntity(sequenceId);
        var role = roleService.getRoleEntity(request.getRoleId());
        sequence.modify(request, role);
        return sequence;
    }

    @Transactional
    public Sequence modifySequence(Long workflowId, Long sequenceId, SequenceRequest request) {
        var sequence = modifySequence(sequenceId, request);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var workspace = sequence.getWorkspace();
        var role = roleService.getRoleEntity(request.getRoleId());
        sequence.modify(request, role);


        return sequence;
    }

    public void deleteSequence(Long sequenceId) {
        var sequence = getSequenceEntity(sequenceId);
        sequenceRepository.delete(sequence);
    }


    public Sequence getSequenceEntity(Long sequenceId) {
        return sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS));
    }

    @Transactional
    public SequenceResponse updateSequence(Long sequenceId, SequenceUpdateRequest request) {
        var sequence = getSequenceEntity(sequenceId);
        if(request.getName()!=null)
            sequence.setName(request.getName());
        if(request.getDayOffset()!=null)
            sequence.setDayOffset(request.getDayOffset());
        if(request.getRoleId()!=null)
            sequence.setRole(roleService.getRoleEntity(request.getRoleId()));
        return SequenceResponse.of(sequence);
    }
}
