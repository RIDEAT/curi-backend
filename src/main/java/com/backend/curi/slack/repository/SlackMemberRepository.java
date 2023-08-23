package com.backend.curi.slack.repository;


import com.backend.curi.slack.repository.entity.SlackMemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlackMemberRepository extends JpaRepository<SlackMemberInfo, Long> {
    Optional<SlackMemberInfo> findByMemberId(Long memberId);
}
