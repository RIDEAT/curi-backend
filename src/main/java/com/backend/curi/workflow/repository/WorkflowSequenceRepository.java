package com.backend.curi.workflow.repository;


import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowSequenceRepository extends JpaRepository<WorkflowSequence, Long> {
    Optional<WorkflowSequence> findByWorkflowAndSequence(Workflow workflow, Sequence sequence);
}
