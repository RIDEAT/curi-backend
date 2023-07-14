package com.backend.curi.user.repository;

import com.backend.curi.user.repository.entity.User_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User_, String> {
    Optional<User_> findByUserId(String userId);

}
