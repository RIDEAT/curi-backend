package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    List<Workflow> findAllByWorkspace(Workspace workspace);
}
