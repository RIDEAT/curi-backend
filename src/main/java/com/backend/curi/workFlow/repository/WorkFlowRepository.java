package com.backend.curi.workFlow.repository;

import com.backend.curi.workFlow.repository.entity.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowRepository extends JpaRepository<WorkFlow, Integer> {

}
