package com.backend.curi.member.repository;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmailAndWorkspace(String email, Workspace workspace);
    List<Employee> findAllByWorkspace(Workspace workspace);
}
