package com.backend.curi.workflow.repository;

import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.curi.workflow.repository.entity.Module;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findAllByWorkspace(Workspace workspace);
}
