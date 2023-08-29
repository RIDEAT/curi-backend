package com.backend.curi.member.repository;

import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByWorkspace(Workspace workspace);
    List<Member> findAllByWorkspaceAndType(Workspace workspace, MemberType type);


}
