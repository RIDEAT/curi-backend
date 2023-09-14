package com.backend.curi.workspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workspace.controller.dto.RoleRequest;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.RoleRepository;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final WorkspaceRepository workspaceRepository;

    public Role getRoleEntity(Long roleId){
        return roleRepository.findById(roleId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.ROLE_NOT_EXISTS));
    }

    public Optional<Role> getRoleEntity(String name, Workspace workspace){
        return roleRepository.findByNameAndWorkspace(name, workspace);
    }

    public RoleResponse getRole(Long roleId){
        Role role = getRoleEntity(roleId);
        return RoleResponse.of(role);
    }

    public List<RoleResponse> getRoles(Long workspaceId){
        List <Role> roles = roleRepository.findAllByWorkspaceIdOrderById(workspaceId);
        return roles.stream().map(RoleResponse::of).collect(Collectors.toList());
    }

    public RoleResponse createRole(Long workspaceId, RoleRequest roleRequest){
        var workspace = getWorkspaceEntityById(workspaceId);
        var role = Role.builder().workspace(workspace).name(roleRequest.getName()).build();
        roleRepository.save(role);
        return RoleResponse.of(role);
    }

    public Role copyRole(Workspace workspace, Role role){
        var roleCopy = Role.builder().workspace(workspace).name(role.getName()).build();
        return roleRepository.save(roleCopy);
    }

    @Transactional
    public RoleResponse updateRole (Long roleId, RoleRequest roleRequest){
        Role role = getRoleEntity(roleId);
        role.modify(roleRequest);
        return RoleResponse.of(role);
    }


    private Workspace getWorkspaceEntityById(Long id){
        return workspaceRepository.findById(id).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }

}
