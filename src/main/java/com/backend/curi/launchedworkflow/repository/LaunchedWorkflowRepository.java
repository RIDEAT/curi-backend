package com.backend.curi.launchedworkflow.repository;

import com.backend.curi.launchedworkflow.repository.entity.LaunchedWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchedWorkflowRepository extends JpaRepository<LaunchedWorkflow, Long> {
}
