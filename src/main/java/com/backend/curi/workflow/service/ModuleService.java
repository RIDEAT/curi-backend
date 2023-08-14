package com.backend.curi.workflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.ModuleRepository;
import com.backend.curi.workflow.repository.SequenceModuleRepository;
import com.backend.curi.workflow.repository.entity.*;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {

    private final WorkspaceService workspaceService;
    private final SequenceService sequenceService;
    private final ModuleRepository moduleRepository;
    private final SequenceModuleRepository sequenceModuleRepository;
    private final ContentRepository contentRepository;


    public ModuleResponse getModule (Long workspaceId, Long moduleId){
        log.info("get module");
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var module = getModuleEntity(moduleId);
        // var module = moduleRepository.findByWorkspaceAndId(workspace, moduleId).orElseThrow(()-> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS));
        return ModuleResponse.of(module);
    }
    public List<ModuleResponse> getModules(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var modules = moduleRepository.findAllByWorkspace(workspace);
        return modules.stream().map(ModuleResponse::of).collect(Collectors.toList());
    }
    @Transactional
    public Module createModule(Long workspaceId, ModuleRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        var content = Content.builder().message(request.getMessage()).build();
        contentRepository.save(content);

        var module = Module.of(request, workspace, content.getId());
        moduleRepository.save(module);

        return module;
    }

    @Transactional
    public ModuleResponse createModule(Long workspaceId, Long sequenceId, ModuleRequest request) {
        var module = createModule(workspaceId, request);
        var sequence = sequenceService.getSequenceEntity(sequenceId);

        var checkRelation = sequenceModuleRepository.findBySequenceAndModule(sequence, module);
        if(checkRelation.isPresent()){
            throw new CuriException(HttpStatus.CONFLICT, ErrorType.SEQUENCE_MODULE_ALREADY_EXISTS);
        }

        var sequenceModule = SequenceModule.builder()
                .sequence(sequence)
                .module(module)
                .orderNum(request.getOrder())
                .build();
        sequenceModuleRepository.save(sequenceModule);
        return ModuleResponse.of(module);
    }

    @Transactional
    public Module modifyModule(Long moduleId, ModuleRequest request) {
        var module = getModuleEntity(moduleId);
        module.modify(request);

        log.info("content id: {}", module.getContentId());

        var content = contentRepository.findById(module.getContentId())
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        content.modify(request);

        return module;
    }

    @Transactional
    public void modifyModule(Long sequenceId, Long moduleId, ModuleRequest request) {
        var module = modifyModule(moduleId, request);
        var sequence = sequenceService.getSequenceEntity(sequenceId);

        var sequenceModule = sequenceModuleRepository.findBySequenceAndModule(sequence, module);
        if(sequenceModule.isEmpty()){
            var newSequenceModule = SequenceModule.builder()
                    .sequence(sequence)
                    .module(module)
                    .orderNum(request.getOrder())
                    .build();
            sequenceModuleRepository.save(newSequenceModule);
        }
        else{
            sequenceModule.get().modify(request);
        }
    }

    public void deleteModule(Long moduleId) {
        var module = getModuleEntity(moduleId);
        moduleRepository.delete(module);
    }

    public void deleteSequenceModule(Long sequenceId, Long moduleId) {
        var module = getModuleEntity(moduleId);
        var sequence = sequenceService.getSequenceEntity(sequenceId);
        var sequenceModule = getSequenceModule(sequence, module);
        sequenceModuleRepository.delete(sequenceModule);
    }


    public Module getModuleEntity(Long moduleId) {
        log.info("module id: {}",moduleId );
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS));
    }

    private SequenceModule getSequenceModule(Sequence sequence, Module module){
        return sequenceModuleRepository.findBySequenceAndModule(sequence, module)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_MODULE_NOT_EXISTS));
    }
}
