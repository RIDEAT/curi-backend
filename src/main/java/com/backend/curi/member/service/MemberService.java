package com.backend.curi.member.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.controller.dto.EmployeeListResponse;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.EmployeeResponse;
import com.backend.curi.member.repository.EmployeeRepository;
import com.backend.curi.member.repository.ManagerRepository;
import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final WorkspaceService workspaceService;

    public EmployeeResponse createEmployee(CurrentUser currentUser, int workspaceId, EmployeeRequest request) {
        var workspace = workspaceService.getWorkspaceById(workspaceId);

        Employee employee = Employee.builder()
                .workspace(workspace)
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .startDate(LocalDate.parse(request.getStartDate()))
                .build();

        employeeRepository.save(employee);
        return EmployeeResponse.ofSuccess(employee);
    }

    public EmployeeResponse getEmployee(CurrentUser currentUser, int workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getEmail(), workspaceId);
        return EmployeeResponse.ofSuccess(employee);
    }

    public EmployeeListResponse getEmployeeList(CurrentUser currentUser, int workspaceId) {
        var workspace = workspaceService.getWorkspaceById(workspaceId);
        var employeeList = employeeRepository.findAllByWorkspace(workspace);
        return EmployeeListResponse.ofSuccess(employeeList);
    }

    @Transactional
    public EmployeeResponse modifyEmployee(CurrentUser currentUser, int workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getEmail(), workspaceId);
        employee.modifyInformation(request.getName(), request.getEmail(), request.getPhoneNum(), LocalDate.parse(request.getStartDate()));
        return EmployeeResponse.ofSuccess(employee);
    }

    public EmployeeResponse deleteEmployee(CurrentUser currentUser, int workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getEmail(), workspaceId);
        employeeRepository.delete(employee);
        return EmployeeResponse.ofSuccess(employee);
    }

    private Employee getEmployeeEntity(String email, int workspaceId) {
        var workspace = workspaceService.getWorkspaceById(workspaceId);
        return employeeRepository.findByEmailAndWorkspace(email, workspace)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
    }



}
