package com.backend.curi.launchedmodule.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launchedmodule.controller.dto.LaunchedModuleRequest;
import com.backend.curi.launchedmodule.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launchedmodule.repository.LaunchedModuleRepository;
import com.backend.curi.launchedmodule.repository.entity.LaunchedModule;
import com.backend.curi.launchedsequence.repository.LaunchedSequenceRepository;
import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LaunchedModuleService {
    private final LaunchedModuleRepository launchedModuleRepository;
    private final LaunchedSequenceRepository launchedSequenceRepository;

    public LaunchedModuleResponse getLaunchedModule(Long moduleId) {
        Optional<LaunchedModule> launchedModule = launchedModuleRepository.findById(moduleId);
        if (launchedModule.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS);
        return LaunchedModuleResponse.of(launchedModule.get());
    }

    public LaunchedModuleResponse createLaunchedModule(LaunchedModuleRequest launchedModuleRequest) {
        LaunchedSequence launchedSequence = launchedSequenceRepository.findById(launchedModuleRequest.getLaunchedSequenceId())
                .orElseThrow(()-> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS));
        LaunchedModule newLaunchedModule = LaunchedModule.of(launchedModuleRequest, launchedSequence);
        LaunchedModule savedLaunchedModule = launchedModuleRepository.save(newLaunchedModule);
        return LaunchedModuleResponse.of(savedLaunchedModule);
    }
}
