package com.backend.curi.workspace.repository;

import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {
    Optional<Workspace> findByWorkspaceId(int workspaceId);
    Optional<Workspace> findByName(String name);
}

