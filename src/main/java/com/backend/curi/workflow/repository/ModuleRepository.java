package com.backend.curi.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.curi.workflow.repository.entity.Module;

public interface ModuleRepository extends JpaRepository<Module, Long> {

}
