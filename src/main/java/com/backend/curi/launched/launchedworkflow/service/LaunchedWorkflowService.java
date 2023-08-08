package com.backend.curi.launched.launchedworkflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.launchedworkflow.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.launched.launchedworkflow.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.launchedworkflow.repository.LaunchedWorkflowRepository;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workflow.service.SequenceService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchedWorkflowService {
    private final LaunchedWorkflowRepository launchedWorkflowRepository;


    public List<LaunchedWorkflowResponse> getLaunchedWorkflowList (Long workspaceId){
        List<LaunchedWorkflow> launchedWorkflowList = launchedWorkflowRepository.findAllByWorkspaceId(workspaceId);
        return launchedWorkflowList.stream().map(LaunchedWorkflowResponse::of).collect(Collectors.toList());
    }
    public LaunchedWorkflow getLaunchedWorkflowEntity (Long launchedWorkflowId){
        Optional<LaunchedWorkflow> launchedWorkflow = launchedWorkflowRepository.findById(launchedWorkflowId);
        if (launchedWorkflow.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);
        return launchedWorkflow.get();
    }
    public LaunchedWorkflowResponse getLaunchedWorkflow(Long workspaceId, Long launchedWorkflowId) {
        Optional<LaunchedWorkflow> launchedWorkflow = launchedWorkflowRepository.findById(launchedWorkflowId);
        if (launchedWorkflow.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);
        return LaunchedWorkflowResponse.of(launchedWorkflow.get());
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

    public LaunchedWorkflowResponse updateLaunchedWorkflow(Long workspaceId, Long launchedWorkflowId, LaunchedWorkflowRequest launchedWorkflowRequestRequest ) {
        //Employee employee = memberService.getEmployeeById(createdLaunchedWorkflow.getEmployeeId());
        //Workspace workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        //Workflow workflow = workflowService.getWorkflowById(createdLaunchedWorkflow.getWorkflowId());


        LaunchedWorkflow existedLaunchedWorkflow = getLaunchedWorkflowEntity(launchedWorkflowId);
        existedLaunchedWorkflow.modify(launchedWorkflowRequestRequest);

       //LaunchedWorkflow savedLaunchedWorkflow = launchedWorkflowRepository.save(newLaunchedWorkflow);
        return LaunchedWorkflowResponse.of(existedLaunchedWorkflow);
    }


    public LaunchedWorkflowResponse saveLaunchedWorkflow (LaunchedWorkflow launchedWorkflow){
        LaunchedWorkflow savedLaunchedWorkflow = launchedWorkflowRepository.save(launchedWorkflow);
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
