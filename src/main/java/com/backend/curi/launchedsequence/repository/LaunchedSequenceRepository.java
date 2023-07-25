package com.backend.curi.launchedsequence.repository;

import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchedSequenceRepository extends JpaRepository<LaunchedSequence, Long> {
}
