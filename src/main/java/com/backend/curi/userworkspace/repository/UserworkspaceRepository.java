package com.backend.curi.userworkspace.repository;

import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserworkspaceRepository extends JpaRepository<Userworkspace, Integer> {
    List<Userworkspace> findAllByUserId(String userId);
    List<Userworkspace> findAllByUserEmail(String userEmail);
    List<Userworkspace> findAllByWorkspaceId(int workspaceId);

    List<Userworkspace> findAllByUserIdAndWorkspaceId(String userId, int workspaceId);
}
