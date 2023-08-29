package com.backend.curi.userworkspace.repository;

import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserworkspaceRepository extends JpaRepository<Userworkspace, Integer> {
    List<Userworkspace> findAllByUser(User_ user);
    List<Userworkspace> findAllByWorkspaceId(Long workspaceId);

    List<Userworkspace> findAllByUserAndWorkspace(User_ user, Workspace workspace);

}
