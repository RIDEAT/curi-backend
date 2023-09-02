package com.backend.curi.launched.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.controller.dto.LaunchedSequenceUpdateRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowUpdateRequest;
import com.backend.curi.launched.repository.LaunchedWorkflowManagerRepository;
import com.backend.curi.launched.repository.LaunchedWorkflowRepository;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.repository.entity.LaunchedWorkflowManager;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workspace.repository.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchedWorkflowService {
    private final LaunchedWorkflowRepository launchedWorkflowRepository;
    private final LaunchedWorkflowManagerRepository launchedWorkflowManagerRepository;

    public List<LaunchedWorkflowResponse> getLaunchedWorkflowList (Long workspaceId){
        List<LaunchedWorkflow> launchedWorkflowList = launchedWorkflowRepository.findAllByWorkspaceId(workspaceId);
        return launchedWorkflowList.stream().map(LaunchedWorkflowResponse::of).collect(Collectors.toList());
    }
    public LaunchedWorkflow getLaunchedWorkflowEntity (Long launchedWorkflowId){
        Optional<LaunchedWorkflow> launchedWorkflow = launchedWorkflowRepository.findById(launchedWorkflowId);
        if (launchedWorkflow.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);
        return launchedWorkflow.get();
    }

    public LaunchedWorkflowResponse getLaunchedWorkflow(Long launchedWorkflowId) {
        Optional<LaunchedWorkflow> launchedWorkflow = launchedWorkflowRepository.findById(launchedWorkflowId);
        if (launchedWorkflow.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);
        return LaunchedWorkflowResponse.of(launchedWorkflow.get());
    }

    public List<LaunchedWorkflow> getLaunchedWorkflowEntityListByWorkflowId (Long workflowId){
        return launchedWorkflowRepository.findAllByWorkflowId(workflowId);
    }

    public List<LaunchedWorkflowResponse> getLaunchedWorkflowListByWorkflowId(Long workflowId) {
        List<LaunchedWorkflow> launchedWorkflowList = getLaunchedWorkflowEntityListByWorkflowId(workflowId);
        return launchedWorkflowList.stream().map(LaunchedWorkflowResponse::of).collect(Collectors.toList());
    }



    public LaunchedWorkflowResponse createLaunchedWorkflow(Long workspaceId, LaunchedWorkflowRequest createdLaunchedWorkflow) {
        //Employee employee = memberService.getEmployeeById(createdLaunchedWorkflow.getEmployeeId());
        //Workspace workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        //Workflow workflow = workflowService.getWorkflowById(createdLaunchedWorkflow.getWorkflowId());



        LaunchedWorkflow newLaunchedWorkflow = LaunchedWorkflow.of(createdLaunchedWorkflow);
        LaunchedWorkflow savedLaunchedWorkflow = launchedWorkflowRepository.save(newLaunchedWorkflow);
        return LaunchedWorkflowResponse.of(savedLaunchedWorkflow);
    }

    @Transactional
    public LaunchedWorkflowResponse modifyLaunchedWorkflow(Long workspaceId, Long launchedWorkflowId, LaunchedWorkflowRequest launchedWorkflowRequestRequest ) {
        //Employee employee = memberService.getEmployeeById(createdLaunchedWorkflow.getEmployeeId());
        //Workspace workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        //Workflow workflow = workflowService.getWorkflowById(createdLaunchedWorkflow.getWorkflowId());


        LaunchedWorkflow existedLaunchedWorkflow = getLaunchedWorkflowEntity(launchedWorkflowId);
        existedLaunchedWorkflow.modify(launchedWorkflowRequestRequest);

        //LaunchedWorkflow savedLaunchedWorkflow = launchedWorkflowRepository.save(newLaunchedWorkflow);
        return LaunchedWorkflowResponse.of(existedLaunchedWorkflow);
    }
    @Transactional
    public LaunchedWorkflowResponse updateLaunchedWorkflow(Long workspaceId, Long launchedWorkflowId, LaunchedWorkflowUpdateRequest request ) {
        LaunchedWorkflow existedLaunchedWorkflow = getLaunchedWorkflowEntity(launchedWorkflowId);
        if(request.getStatus() != null)
            existedLaunchedWorkflow.setStatus(request.getStatus());
        if(request.getKeyDate() != null) {
            existedLaunchedWorkflow.setKeyDate(request.getKeyDate());
            // are sequences apply date need update?
        }
        return LaunchedWorkflowResponse.of(existedLaunchedWorkflow);
    }

    @Transactional
    public LaunchedWorkflowResponse saveLaunchedWorkflow (LaunchedWorkflow launchedWorkflow, Map<Role, Member> managers){
        LaunchedWorkflow savedLaunchedWorkflow = launchedWorkflowRepository.save(launchedWorkflow);

        for (var manager : managers.entrySet()){
            var launchedWorkflowManager =
                    launchedWorkflowManagerRepository.save(LaunchedWorkflowManager.of(savedLaunchedWorkflow, manager.getKey(), manager.getValue()));
            savedLaunchedWorkflow.getLaunchedWorkflowManagers().add(launchedWorkflowManager);
        }
        return LaunchedWorkflowResponse.of(savedLaunchedWorkflow);
    }

    /*
    public LaunchedWorkflowResponse updateLaunchedWorkflow(String workspaceId, Long launchedworkflowId, LaunchedWorkflow updatedLaunchedWorkflow) {
        // Implement the logic to update an existing launched workflow based on the IDs.
        LaunchedWorkflow existingLaunchedWorkflow = launchedWorkflowRepository.findByIdAndWorkspaceId(launchedworkflowId, workspaceId);
        if (existingLaunchedWorkflow == null) {
            return null; // Workflow not found.
        }

        // Update the existing workflow entity with the data from the updatedLaunchedWorkflow.
        existingLaunchedWorkflow.setSomeField(updatedLaunchedWorkflow.getSomeField());
        // ... continue updating other fields as needed ...

        LaunchedWorkflow updatedWorkflow = launchedWorkflowRepository.save(existingLaunchedWorkflow);
        // Convert the updated entity to the response DTO.
        return convertToLaunchedWorkflowResponse(updatedWorkflow);
    }
*/

    public void deleteLaunchedWorkflow(Long launchedworkflowId) {
        // Implement the logic to delete a specific launched workflow based on the IDs.
        LaunchedWorkflow existingLaunchedWorkflow = launchedWorkflowRepository.findById(launchedworkflowId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
        launchedWorkflowRepository.delete(existingLaunchedWorkflow);

    }



}
