package com.backend.curi.member.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.EmployeeRepository;
import com.backend.curi.member.repository.ManagerRepository;
import com.backend.curi.member.repository.MemberRepository;
import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.Manager;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceService workspaceService;

    public EmployeeResponse createEmployee(CurrentUser currentUser, Long workspaceId, EmployeeRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        Employee employee = Employee.builder()
                .workspace(workspace)
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .startDate(LocalDate.parse(request.getStartDate()))
                .build();

        employeeRepository.save(employee);
        return EmployeeResponse.of(employee);
    }

    public EmployeeResponse getEmployee(CurrentUser currentUser, Long workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getId(), workspaceId);
        return EmployeeResponse.of(employee);
    }

    public EmployeeListResponse getEmployeeList(CurrentUser currentUser, Long workspaceId) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var employeeList = employeeRepository.findAllByWorkspace(workspace);
        return EmployeeListResponse.of(employeeList);
    }

    @Transactional
    public EmployeeResponse modifyEmployee(CurrentUser currentUser, Long workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getId(), workspaceId);
        employee.modifyInformation(request.getName(), request.getEmail(), request.getPhoneNum(), LocalDate.parse(request.getStartDate()));
        return EmployeeResponse.of(employee);
    }

    public EmployeeResponse deleteEmployee(CurrentUser currentUser, Long workspaceId, EmployeeRequest request) {
        var employee = getEmployeeEntity(request.getId(), workspaceId);
        employeeRepository.delete(employee);
        return EmployeeResponse.of(employee);
    }

    private Employee getEmployeeEntity(Long id, Long workspaceId) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
        if (!employee.getWorkspace().equals(workspace))
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);

        return employee;
    }


    public ManagerResponse createManager(CurrentUser currentUser, Long workspaceId, ManagerRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        Manager manager = Manager.builder()
                .workspace(workspace)
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .startDate(LocalDate.parse(request.getStartDate()))
                .department(request.getDepartment())
                .build();

        managerRepository.save(manager);
        return ManagerResponse.of(manager);
    }

    public ManagerResponse getManager(CurrentUser currentUser, Long workspaceId, ManagerRequest request) {
        var employee = getManagerEntity(request.getId(), workspaceId);
        return ManagerResponse.of(employee);
    }

    public ManagerListResponse getManagerList(CurrentUser currentUser, Long workspaceId) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var managerList = managerRepository.findAllByWorkspace(workspace);
        return ManagerListResponse.of(managerList);
    }

    @Transactional
    public ManagerResponse modifyManager(CurrentUser currentUser, Long workspaceId, ManagerRequest request) {
        var manager = getManagerEntity(request.getId(), workspaceId);
        manager.modifyInformation(request.getName(), request.getEmail(), request.getPhoneNum(), LocalDate.parse(request.getStartDate()), request.getDepartment());
        return ManagerResponse.of(manager);
    }

    public ManagerResponse deleteManager(CurrentUser currentUser, Long workspaceId, ManagerRequest request) {
        var manager = getManagerEntity(request.getId(), workspaceId);
        managerRepository.delete(manager);
        return ManagerResponse.of(manager);
    }

    private Manager getManagerEntity(Long id, Long workspaceId) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var manager = managerRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
        if (!manager.getWorkspace().equals(workspace))
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);

        return manager;
    }


    public MemberListResponse getMemberList (CurrentUser currentUser, Long workspaceId, MemberType memberType){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var memberList = memberRepository.findAllByWorkspaceAndType(workspace, memberType);
        return MemberListResponse.of(memberList);
    }

    public MemberResponse createMember (CurrentUser currentUser, MemberRequest request){

        var workspace = workspaceService.getWorkspaceEntityById(request.getWid());

        Member member = Member.builder()
                .workspace(workspace)
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .department(request.getDepartment())
                .startDate(LocalDate.parse(request.getStartDate()))
                .type(request.getType())
                .build();

        memberRepository.save(member);

        // member 가 employee 냐 manager 에 따라 추가 정보 저장해야 함.

        return MemberResponse.of(member);
    }

}
