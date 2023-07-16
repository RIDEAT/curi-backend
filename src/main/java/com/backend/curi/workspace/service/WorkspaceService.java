package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.controller.dto.WorkspaceListResponse;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.curi.workspace.repository.WorkspaceRepository;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserworkspaceService userworkspaceService;

    public WorkspaceResponse getWorkspaceById(Long id){
        var workspace = workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
        return WorkspaceResponse.ofSuccess(workspace);
    }

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, CurrentUser currentUser){

        // 이름 중복 검사
        if(workspaceRepository.findByName(request.getName()).isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.DUPLICATED_WORKSPACE_NAME);
        }

        // 하나의 유저가 여러 개의 워크스페이스를 만들 수 있다?

        Workspace workspace = Workspace.builder().name(request.getName()).email(request.getEmail()).build();
        // workspace db 에 id 가 순서대로 올라가는지 확인해야한다.
        workspaceRepository.save(workspace);
        userworkspaceService.create(currentUser.getUserId(), workspace);

        return WorkspaceResponse.ofSuccess(workspace);
    }

    @Transactional
    public WorkspaceResponse updateWorkspace (Long workspaceId, CurrentUser currentUser, WorkspaceRequest request){
        var workspace = getWorkspaceEntityById(workspaceId);

        // 수정 권한이 있는 사람만 확인하는 로직
        userworkspaceService.checkAuthentication(currentUser.getUserId(), workspace);

        workspace.setName(request.getName());
        workspace.setEmail(request.getEmail());

        return WorkspaceResponse.ofSuccess(workspace);
    }

    @Transactional
    public WorkspaceResponse deleteWorkspace(Long workspaceId, CurrentUser currentUser){
        var workspace = getWorkspaceEntityById(workspaceId);
        // 수정 권한이 있는 사람만 확인하는 로직
        userworkspaceService.checkAuthentication(currentUser.getUserId(), workspace);

        log.info("User {} is deleting workspace {}", currentUser.getUserId(), workspaceId);

        /*
        if(!existingWorkspace.getUserId().equals(userId)){
            throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.UNAUTHORIZED_WORKSPACE);
        }*/
        // 작업 공간 삭제
        workspaceRepository.delete(workspace);

        return WorkspaceResponse.ofSuccess(workspace);

    }

    public WorkspaceListResponse getWorkspaceList(CurrentUser currentUser){
        var workspaceList = userworkspaceService.getWorkspaceListByUser(currentUser.getUserId());

        return WorkspaceListResponse.ofSuccess(workspaceList);
    }

    public Workspace getWorkspaceEntityById(Long id){
        return workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }

}
