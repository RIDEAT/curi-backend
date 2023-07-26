package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SequenceModuleRepository extends JpaRepository<SequenceModule, Long> {
    Optional<SequenceModule> findBySequenceAndModule(Sequence sequence, Module module);
}
