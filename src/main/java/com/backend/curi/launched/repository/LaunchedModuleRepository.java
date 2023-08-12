package com.backend.curi.launched.repository;

import com.backend.curi.launched.repository.entity.LaunchedModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaunchedModuleRepository extends JpaRepository<LaunchedModule, Long> {
}
