package com.backend.curi.workspace.repository;

import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByIdAndWorkspace(Long id, Workspace workspace);
    List<Role> findAllByWorkspaceIdOrderById(Long workspaceId);
    Optional<Role> findByNameAndWorkspace(String name, Workspace workspace);
}
