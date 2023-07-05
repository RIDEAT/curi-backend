package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Integer> {

}
