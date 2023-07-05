package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.curi.workspace.repository.WorkspaceRepository;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;


    public int createWorkspace(WorkspaceForm workspaceForm){
        // 이름 중복 검사
        if(workspaceRepository.findByName(workspaceForm.getName()).isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.DUPLICATED_WORKSPACE_NAME);
        }

        // 하나의 유저가 여러 개의 워크스페이스를 만들 수 있다?


        Workspace workspace = Workspace.builder().name(workspaceForm.getName()).build();
        // workspace db 에 id 가 순서대로 올라가는지 확인해야한다.
        workspaceRepository.save(workspace);
        return workspace.getWorkspaceId();
    }

    public String getWorkSpaceNameByWorkspaceId(int workSpaceId){
        return workspaceRepository.findByWorkspaceId(workSpaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS)).getName();


    }



}
