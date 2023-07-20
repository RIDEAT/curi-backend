package com.backend.curi.member.repository;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.EmployeeManager;
import com.backend.curi.member.repository.entity.Manager;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.workspace.repository.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeManagerRepository extends JpaRepository<EmployeeManager, Long> {
    List<EmployeeManager> findAllByEmployee(Employee employee);
    List<EmployeeManager> findAllByManager(Manager manager);
    Optional<EmployeeManager> findByEmployeeAndManager(Employee employee, Manager manager);
}
