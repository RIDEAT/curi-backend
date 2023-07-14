package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.curi.workspace.repository.WorkspaceRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserworkspaceService userworkspaceService;

    public Workspace getWorkspaceById(int workspaceId){
        return workspaceRepository.findById(workspaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }

    @Transactional
    public Workspace createWorkspace(WorkspaceForm workspaceForm, CurrentUser currentUser){

        // 이름 중복 검사
        if(workspaceRepository.findByName(workspaceForm.getName()).isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.DUPLICATED_WORKSPACE_NAME);
        }

        // 하나의 유저가 여러 개의 워크스페이스를 만들 수 있다?

        Workspace workspace = Workspace.builder().name(workspaceForm.getName()).email(workspaceForm.getEmail()).build();
        // workspace db 에 id 가 순서대로 올라가는지 확인해야한다.
        workspaceRepository.save(workspace);
        userworkspaceService.create(currentUser.getUserId(), workspace);

        return workspace;
    }

    @Transactional
    public Workspace updateWorkspace (int workspaceId, CurrentUser currentUser, WorkspaceForm form){
        var workspace = getWorkspaceById(workspaceId);

        // 수정 권한이 있는 사람만 확인하는 로직
        if (!userworkspaceService.exist(currentUser.getUserId(), workspace)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }
        workspace.setName(form.getName());
        workspace.setEmail(form.getEmail());

        return workspace;
    }

    @Transactional
    public int deleteWorkspace(int workspaceId, CurrentUser currentUser){
        var workspace = getWorkspaceById(workspaceId);
        // 수정 권한이 있는 사람만 확인하는 로직
        if (!userworkspaceService.exist(currentUser.getUserId(), workspace)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }
        log.info("User {} is deleting workspace {}", currentUser.getUserId(), workspaceId);

        Workspace existingWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));

        /*
        if(!existingWorkspace.getUserId().equals(userId)){
            throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.UNAUTHORIZED_WORKSPACE);
        }*/
        // 작업 공간 삭제
        workspaceRepository.delete(existingWorkspace);
        userworkspaceService.delete(currentUser.getUserId(), workspace);

        return workspaceId;

    }

    public List<Workspace> getWorkspaceList(CurrentUser currentUser){
        List<Workspace> workspaceList = userworkspaceService.getWorkspaceListByUser(currentUser.getUserId());

        return workspaceList;
    }



}
