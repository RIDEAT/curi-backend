package com.backend.curi.launched.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.controller.dto.LaunchedModuleRequest;
import com.backend.curi.launched.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.LaunchedModuleRepository;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LaunchedModuleService {
    private final LaunchedModuleRepository launchedModuleRepository;
    private final LaunchedSequenceService launchedSequenceService;

    public LaunchedModuleResponse getLaunchedModule(Long moduleId) {
        LaunchedModule launchedModule = getLaunchedModuleEntity(moduleId);
        return LaunchedModuleResponse.of(launchedModule);
    }

    public LaunchedModule getLaunchedModuleEntity(Long moduleId){
        return launchedModuleRepository.findById(moduleId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS));
    }

    public LaunchedModuleResponse createLaunchedModule(LaunchedModuleRequest launchedModuleRequest) {
        LaunchedSequence launchedSequence = launchedSequenceService.getLaunchedSequenceEntity(launchedModuleRequest.getLaunchedSequenceId());
        LaunchedModule launchedModule = LaunchedModule.of(launchedModuleRequest, launchedSequence);
        LaunchedModule savedLaunchedModule = launchedModuleRepository.save(launchedModule);
        return LaunchedModuleResponse.of(savedLaunchedModule);
    }

    public LaunchedModule completeLaunchedModule (LaunchedModule launchedModule){
        launchedModule.setStatus(LaunchedStatus.COMPLETED);
        LaunchedModule savedModule = launchedModuleRepository.save(launchedModule);
        return savedModule;
    }

    @Transactional
    public LaunchedModuleResponse updateLaunchedModule(LaunchedModuleRequest launchedModuleRequest, Long moduleId){
        LaunchedModule launchedModule = getLaunchedModuleEntity(moduleId);
        launchedModule.modify(launchedModuleRequest);
        return LaunchedModuleResponse.of(launchedModule);
    }

    public void deleteLaunchedModule (Long moduleId){
        LaunchedModule launchedModule = getLaunchedModuleEntity(moduleId);
        launchedModuleRepository.delete(launchedModule);
    }
    public LaunchedModuleResponse saveLaunchedModule (LaunchedModule launchedModule){
        LaunchedModule savedLaunchedModule = launchedModuleRepository.save(launchedModule);
        return LaunchedModuleResponse.of(savedLaunchedModule);
    }

    public LaunchedModule startLaunchedModule(LaunchedModule launchedModule) {
        launchedModule.setStatus(LaunchedStatus.IN_PROGRESS);
        LaunchedModule savedModule = launchedModuleRepository.save(launchedModule);
        return savedModule;
    }
}
