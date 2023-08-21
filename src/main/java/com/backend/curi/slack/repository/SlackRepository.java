package com.backend.curi.slack.repository;


import com.backend.curi.slack.repository.entity.SlackToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlackRepository extends JpaRepository<SlackToken, Long> {
    Optional<SlackToken> findByUserId(String userId);

}
