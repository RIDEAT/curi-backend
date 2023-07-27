package com.backend.curi.launched.launchedworkflow.repository;

import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchedWorkflowRepository extends JpaRepository<LaunchedWorkflow, Long> {
}
