package com.backend.curi.slack.repository;


import com.backend.curi.slack.repository.entity.SlackInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlackRepository extends JpaRepository<SlackInfo, Long> {
    Optional<SlackInfo> findByUserFirebaseId(String userFirebaseId);

}
