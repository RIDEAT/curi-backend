package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workspace.controller.dto.RoleListResponse;
import com.backend.curi.workspace.controller.dto.RoleRequest;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.RoleRepository;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final WorkspaceService workspaceService;

    public Role getRoleEntity(Long roleId){
        return roleRepository.findById(roleId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.ROLE_NOT_EXISTS));
    }
    public RoleListResponse getRoleList(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var roleList = roleRepository.findByWorkspace(workspace);
        return RoleListResponse.of(roleList);
    }
    public RoleResponse createRole(Long workspaceId, RoleRequest roleRequest){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var role = Role.builder().workspace(workspace).name(roleRequest.getName()).build();
        roleRepository.save(role);
        return RoleResponse.of(role);
    }
}