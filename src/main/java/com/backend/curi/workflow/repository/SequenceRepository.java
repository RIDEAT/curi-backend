package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SequenceRepository extends JpaRepository<Sequence, Long>{
}
