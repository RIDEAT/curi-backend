package com.backend.curi.launched.repository;

import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface LaunchedSequenceRepository extends JpaRepository<LaunchedSequence, Long> {
    List<LaunchedSequence> findAllByStatusAndWorkspaceId(LaunchedStatus launchedStatus, Long workspaceId);
    List<LaunchedSequence> findAllByStatus(LaunchedStatus launchedStatus);
    List<LaunchedSequence> findAllByWorkspaceId(Long workspaceId);
}
