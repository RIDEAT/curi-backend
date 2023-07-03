package com.backend.curi.workSpace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workSpace.controller.dto.WorkSpaceForm;
import com.backend.curi.workSpace.repository.entity.WorkSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import com.backend.curi.workSpace.repository.WorkSpaceRepository;

@Service
@RequiredArgsConstructor
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;


    public int createWorkSpace(WorkSpaceForm workSpaceForm){
        // 이름 중복 검사
        if(workSpaceRepository.findByName(workSpaceForm.getName()).isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.DUPLICATED_WORKSPACE_NAME);
        }

        // 하나의 유저가 여러 개의 워크스페이스를 만들 수 있다?


        WorkSpace workSpace = WorkSpace.builder().name(workSpaceForm.getName()).build();
        // workspace db 에 id 가 순서대로 올라가는지 확인해야한다.
        workSpaceRepository.save(workSpace);
        return workSpace.getWorkSpaceId();
    }

    public String getWorkSpaceNameByWorkSpaceId(int workSpaceId){
        return workSpaceRepository.findByWorkSpaceId(workSpaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS)).getName();


    }



}
