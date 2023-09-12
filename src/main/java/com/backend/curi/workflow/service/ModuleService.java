package com.backend.curi.workflow.service;

import com.backend.curi.common.Common;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.controller.dto.ContentUpdateRequest;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.ModuleRepository;
import com.backend.curi.workflow.repository.entity.*;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {

    private final WorkspaceRepository workspaceRepository;
    private final SequenceService sequenceService;
    private final ModuleRepository moduleRepository;
    private final ContentRepository contentRepository;
    private final ContentService contentService;
    private final Common common;

    public ModuleResponse getModule (Long workspaceId, Long moduleId){
        log.info("get module");
        var workspace = getWorkspaceEntityById(workspaceId);
        var module = getModuleEntity(moduleId);
        // var module = moduleRepository.findByWorkspaceAndId(workspace, moduleId).orElseThrow(()-> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS));
        return ModuleResponse.of(module);
    }
    public List<ModuleResponse> getModules(Long workspaceId){
        var workspace = getWorkspaceEntityById(workspaceId);
        var modules = moduleRepository.findAllByWorkspace(workspace);
        return modules.stream().map(ModuleResponse::of).collect(Collectors.toList());
    }
    @Transactional
    public Module createModule(Long workspaceId, Long sequenceId, ModuleRequest request) {
        var workspace = getWorkspaceEntityById(workspaceId);
        var sequence = sequenceService.getSequenceEntity(sequenceId);
        var currentUser = common.getCurrentUser();

        var content = Content.of(request.getType(), currentUser, workspaceId);
        contentRepository.save(content);

        var module = Module. of(request, workspace, sequence, content.getId());
        moduleRepository.save(module);

        return module;
    }

    @Transactional
    public void copyModule(Workspace workSpace, Sequence sequence, Module origin){
        var currentUser = common.getCurrentUser();
        var contentToCopy = contentService.getContent(origin.getContentId());

        var content = Content.of(contentToCopy, currentUser, workSpace.getId());
        contentRepository.save(content);

        var module = Module.of(origin, workSpace, sequence, content.getId());
        moduleRepository.save(module);
    }

    @Transactional
    public Module modifyModule(Long moduleId, ModuleRequest request) {
        var module = getModuleEntity(moduleId);
        module.modify(request);
        return module;
    }

    @Transactional
    public Module updateModule(Long moduleId, ModuleUpdateRequest request) {
        var module = getModuleEntity(moduleId);
        if(request.getName()!=null)
            module.setName(request.getName());
        if(request.getOrder()!=null)
            module.setOrder(request.getOrder());
        return module;
    }

    public void deleteModule(Long moduleId) {
        var module = getModuleEntity(moduleId);
        moduleRepository.delete(module);
    }


    public Module getModuleEntity(Long moduleId) {
        log.info("module id: {}",moduleId );
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS));
    }

    public ContentResponse getContent(Long moduleId){
        var module = getModuleEntity(moduleId);
        var content = contentRepository.findById(module.getContentId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        return ContentResponse.of(content, module);
    }

    @Transactional
    public<T> ContentResponse updateContent(Long moduleId, ContentUpdateRequest<T> request){
        var module = getModuleEntity(moduleId);
        var content = contentRepository.findById(module.getContentId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        var currentUser = common.getCurrentUser();

        content.setContent(request.getContent());
        content.setUpdatedBy(currentUser);
        content.setUpdatedDate(LocalDateTime.now());

        contentRepository.save(content);
        return ContentResponse.of(content, module);
    }
    private Workspace getWorkspaceEntityById(Long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }
}
