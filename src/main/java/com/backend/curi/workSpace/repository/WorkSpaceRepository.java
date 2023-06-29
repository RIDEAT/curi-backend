package com.backend.curi.workSpace.repository;

import com.backend.curi.workSpace.repository.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Integer> {
    Optional<WorkSpace> findByWorkSpaceId(String workSpaceId);
}
