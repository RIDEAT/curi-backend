package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.curi.workspace.repository.WorkspaceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;


    public List<Workspace> getWorkspacesByUserId(String userId){
        return workspaceRepository.findAllByUserId(userId);
    }

    public Workspace getWorkspaceById(int workspaceId){
        return workspaceRepository.findById(workspaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }
    public int createWorkspace(WorkspaceForm workspaceForm, String userId){
        // 이름 중복 검사
        if(workspaceRepository.findByName(workspaceForm.getName()).isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.DUPLICATED_WORKSPACE_NAME);
        }

        // 하나의 유저가 여러 개의 워크스페이스를 만들 수 있다?


        Workspace workspace = Workspace.builder().name(workspaceForm.getName()).userId(userId).build();
        // workspace db 에 id 가 순서대로 올라가는지 확인해야한다.
        workspaceRepository.save(workspace);
        return workspace.getWorkspaceId();
    }

    public int updateWorkspace (Workspace workspace){
        int workspaceId = workspace.getWorkspaceId();

        //refresh 토큰이 있는지 확인
        Optional<Workspace> workspaceInDB = workspaceRepository.findById(workspaceId);


        // 있다면 새토큰 발급후 업데이트
        // 없다면 새로 만들고 디비 저장
      return 0;
    }




}
