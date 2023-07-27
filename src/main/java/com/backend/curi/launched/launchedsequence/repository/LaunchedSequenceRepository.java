package com.backend.curi.launched.launchedsequence.repository;

import com.backend.curi.launched.launchedsequence.repository.entity.LaunchedSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchedSequenceRepository extends JpaRepository<LaunchedSequence, Long> {
}
