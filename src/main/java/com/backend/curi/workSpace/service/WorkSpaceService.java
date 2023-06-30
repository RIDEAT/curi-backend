package com.backend.curi.workSpace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
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


    public void createWorkSpace(int workSpaceId){
        WorkSpace workSpace = WorkSpace.builder().workSpaceId(workSpaceId).build();
        workSpaceRepository.save(workSpace);
    }

    public String getWorkSpaceName(int workSpaceId){
        return workSpaceRepository.findByWorkSpaceId(workSpaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS)).getName();


    }


}
