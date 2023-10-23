package com.backend.curi.launched.repository;

import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaunchedModuleRepository extends JpaRepository<LaunchedModule, Long> {
    List<LaunchedModule> findAllByModuleAndType(Module workspace, ModuleType type);
}
