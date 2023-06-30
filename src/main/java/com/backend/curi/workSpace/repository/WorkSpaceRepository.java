package com.backend.curi.workSpace.repository;

import com.backend.curi.workSpace.repository.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Integer> {
    Optional<WorkSpace> findByWorkSpaceId(int workSpaceId);
}

