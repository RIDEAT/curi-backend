package com.backend.curi.member.repository;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.Manager;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findById(Long id);
    Optional<Manager> findByEmailAndWorkspace(String email, Workspace workspace);
    List<Manager> findAllByWorkspace(Workspace workspace);
}