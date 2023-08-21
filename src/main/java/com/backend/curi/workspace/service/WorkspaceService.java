package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.repository.RoleRepository;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.curi.workspace.repository.WorkspaceRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private static Logger log = LoggerFactory.getLogger(WorkspaceService.class);

    private final WorkspaceRepository workspaceRepository;
    private final UserworkspaceService userworkspaceService;
    private final RoleRepository roleRepository;
    private final WorkflowRepository workflowRepository;
    public WorkspaceResponse getWorkspaceById(Long id){
        log.info("getWorkspaceById");
        var workspace = workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
        return WorkspaceResponse.of(workspace);
    }

    public List<Workspace> getWorkspaceList(CurrentUser currentUser){
        return userworkspaceService.getWorkspaceListByUser(currentUser);
    }

    public Workspace getWorkspaceEntityById(Long id){
        return workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }
    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, CurrentUser currentUser){

        Workspace workspace = Workspace.builder().name(request.getName()).email(request.getEmail()).build();
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        userworkspaceService.create(currentUser, savedWorkspace);

        createDefaultRole(savedWorkspace);
        createDefaultWorkflow(savedWorkspace);

        return WorkspaceResponse.of(savedWorkspace);
    }

    @Transactional
    public WorkspaceResponse updateWorkspace (Long workspaceId, CurrentUser currentUser, WorkspaceRequest request){
        var workspace = getWorkspaceEntityById(workspaceId);
        workspace.setName(request.getName());
        workspace.setEmail(request.getEmail());

        return WorkspaceResponse.of(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long workspaceId, CurrentUser currentUser){
        var workspace = getWorkspaceEntityById(workspaceId);

        log.info("User {} is deleting workspace {}", currentUser.getUserId(), workspaceId);

        /*
        if(!existingWorkspace.getUserId().equals(userId)){
            throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.UNAUTHORIZED_WORKSPACE);
        }*/
        // 작업 공간 삭제
        workspaceRepository.delete(workspace);
    }

    private void createDefaultRole(Workspace workspace) {
        var employeeRole = Role.builder().workspace(workspace).name("신규입사자").build();
        var directManagerRole = Role.builder().workspace(workspace).name("담당사수").build();
        var hrManagerRole = Role.builder().workspace(workspace).name("HR매니저").build();

        roleRepository.save(employeeRole);
        roleRepository.save(directManagerRole);
        roleRepository.save(hrManagerRole);

        workspace.getRoles().add(employeeRole);
        workspace.getRoles().add(directManagerRole);
        workspace.getRoles().add(hrManagerRole);
    }

    private void createDefaultWorkflow(Workspace workspace){
        var employeeWorkflow = Workflow.builder().workspace(workspace).name("신입 공통 워크플로우").build();
        var itWorkflow = Workflow.builder().workspace(workspace).name("it 직무 워크플로우").build();
        var ethicWorkflow = Workflow.builder().workspace(workspace).name("윤리 워크플로우").build();

        workflowRepository.save(employeeWorkflow);
        workflowRepository.save(itWorkflow);
        workflowRepository.save(ethicWorkflow);

        workspace.getWorkflows().add(employeeWorkflow);
        workspace.getWorkflows().add(itWorkflow);
        workspace.getWorkflows().add(ethicWorkflow);

    }







}
