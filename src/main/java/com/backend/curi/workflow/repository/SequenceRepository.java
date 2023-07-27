package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SequenceRepository extends JpaRepository<Sequence, Long>{
    List<Sequence> findAllByWorkspace(Workspace workspace);
}
