package com.backend.curi.workspace.service;

import com.amazonaws.Response;
import com.amazonaws.services.s3.AmazonS3;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.message.service.MessageService;
import com.backend.curi.notification.service.NotificationService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.smtp.AwsS3Service;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.service.WorkflowCopyService;
import com.backend.curi.workspace.controller.dto.LogoPreSignedUrlResponse;
import com.backend.curi.workspace.controller.dto.LogoSignedUrlResponse;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.repository.RoleRepository;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final AwsS3Service amazonS3Service;
    private final MessageService messageService;
    private final SlackService slackService;
    private final WorkflowCopyService workflowCopyService;
  
    public WorkspaceResponse getWorkspaceById(Long id){
        log.info("getWorkspaceById");
        var workspace = workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
        workspace.setLogoUrl(amazonS3Service.getSignedUrl(workspace.getLogoUrl()));
        return WorkspaceResponse.of(workspace);
    }

    public List<Workspace> getWorkspaceList(CurrentUser currentUser){
        return userworkspaceService.getWorkspaceListByUser(currentUser);
    }

    public Workspace getWorkspaceEntityById(Long id){
        return workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }

    public WorkspaceResponse createWorkspace(WorkspaceRequest request, CurrentUser currentUser){
        var savedWorkspace = createWorkspaceEntity(request, currentUser);
        workflowCopyService.copyTemplateWorkflows(savedWorkspace);
        messageService.sendWorkspaceCreateMessage(savedWorkspace, currentUser);
        savedWorkspace.setLogoUrl(amazonS3Service.getSignedUrl(savedWorkspace.getLogoUrl()));
        return WorkspaceResponse.of(savedWorkspace);
    }

    @Transactional
    public Workspace createWorkspaceEntity(WorkspaceRequest request, CurrentUser currentUser){
        Workspace workspace = Workspace.builder().name(request.getName()).email(request.getEmail()).build();
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        userworkspaceService.create(currentUser, savedWorkspace);

        createDefaultRole(savedWorkspace);
        slackService.sendMessageToRideat(new SlackMessageRequest("새로운 워크스페이스가 생성되었습니다. 이름 : " + request.getName() + ", 유저: "+ currentUser.getUserId() + ", 유저이름: " + currentUser.getName()));


        return savedWorkspace;
    }

    @Transactional
    public WorkspaceResponse updateWorkspace (Long workspaceId, WorkspaceRequest request){
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
        var employeeRole = Role.builder().workspace(workspace).name("대상자").build();
        var directManagerRole = Role.builder().workspace(workspace).name("버디").build();
        var hrManagerRole = Role.builder().workspace(workspace).name("HR").build();

        roleRepository.save(employeeRole);
        roleRepository.save(directManagerRole);
        roleRepository.save(hrManagerRole);

        workspace.getRoles().add(employeeRole);
        workspace.getRoles().add(directManagerRole);
        workspace.getRoles().add(hrManagerRole);
    }



    @Transactional
    public LogoPreSignedUrlResponse setWorkspaceLogo(Long workspaceId, String fileName){
        amazonS3Service.isValidimageName(fileName);
        var workspace = getWorkspaceEntityById(workspaceId);
        if(!workspace.getLogoUrl().equals(("default/logo/example_logo.jpeg")))
            amazonS3Service.deleteFile(workspace.getLogoUrl());
        var path = "workspace/" + workspaceId + "/" + fileName;
        var preSignedUrl = amazonS3Service.getPreSignedUrl(path);
        workspace.setLogoUrl(path);
        return new LogoPreSignedUrlResponse(preSignedUrl);
    }

    @Transactional
    public void deleteWorkspaceLogo(Long workspaceId){
        var workspace = getWorkspaceEntityById(workspaceId);
        // 추후 constant로 변경
        if(workspace.getLogoUrl().equals(("default/logo/example_logo.jpeg")))
            return;
        var path = "workspace/" + workspaceId + "/logo.png";
        amazonS3Service.deleteFile(path);
        workspace.setLogoUrl("default/logo/example_logo.jpeg");
    }

    public LogoSignedUrlResponse getWorkspaceLogo(Long workspaceId){
        var workspace = getWorkspaceEntityById(workspaceId);
        return new LogoSignedUrlResponse(amazonS3Service.getSignedUrl(workspace.getLogoUrl()));
    }

    public List<WorkflowResponse> getTemplateWorkflows(){
        return workflowCopyService.getTemplateWorkflows();
    }

    public WorkflowResponse copyTemplateWorkflows(Long workspaceId, Long workflowId){
        var workspace = getWorkspaceEntityById(workspaceId);
        return workflowCopyService.copyWorkflow(workspace, workflowId);
    }

}
