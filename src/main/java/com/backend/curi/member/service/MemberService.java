package com.backend.curi.member.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.EmployeeManagerRepository;
import com.backend.curi.member.repository.EmployeeRepository;
import com.backend.curi.member.repository.ManagerRepository;
import com.backend.curi.member.repository.MemberRepository;
import com.backend.curi.member.repository.entity.*;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.repository.RoleRepository;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final MemberRepository memberRepository;
    private final EmployeeManagerRepository employeeManagerRepository;
    private final RoleRepository roleRepository;
    private final WorkspaceService workspaceService;
    private final UserworkspaceService userworkspaceService;

    public MemberResponse getMember(CurrentUser currentUser, Long memberId) {
        var member = getMemberEntity(memberId, currentUser);
        return MemberResponse.of(member);
    }

    public MemberResponse deleteMember(CurrentUser currentUser, Long memberId) {
        var member = getMemberEntity(memberId, currentUser);
        memberRepository.delete(member);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse modifyMember(CurrentUser currentUser, Long memberId, MemberRequest request) {
        var member = getMemberEntity(memberId, currentUser);
        member.modifyInformation(request);
        modifyEmployeeManager(currentUser, member, request);
        return MemberResponse.of(member);
    }

    public List<MemberResponse> getMemberList(CurrentUser currentUser, Long workspaceId, MemberType memberType) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);
        var memberList = memberRepository.findAllByWorkspaceAndType(workspace, memberType);
        var responseList = memberList.stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
        return responseList;
    }

    @Transactional
    public String createMember(CurrentUser currentUser, MemberType type, MemberRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(request.getWid());
        userworkspaceService.checkAuthentication(currentUser, workspace);


        var memberBuilder = Member.of(request).type(type).workspace(workspace);


        if (type == MemberType.employee) {
            var employee = Employee.of(request).build();
            employeeRepository.save(employee);
            memberBuilder.employee(employee);
        }
        else if (type == MemberType.manager) {
            var manager = Manager.of(request).build();
            managerRepository.save(manager);
            memberBuilder.manager(manager);
        }
        // member 가 employee 냐 manager 에 따라 추가 정보 저장해야 함.

        var member = memberBuilder.build();
        memberRepository.save(member);
        setEmployeeManager(currentUser, member, request);
        return "suceess";
    }

    private Member getMemberEntity(Long id, CurrentUser currentUser) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
        var workspace = member.getWorkspace();

        userworkspaceService.checkAuthentication(currentUser, workspace);

        return member;
    }


    @Transactional
    public void setEmployeeManager(CurrentUser currentUser, Member employee, MemberRequest request) {
        if(employee.getType() != MemberType.employee){
            return;
        }
        var req = (EmployeeRequest)request;
        var list = req.getManagers();
        var workspace = employee.getWorkspace();

        for(var detail : list){
            var manager = getMemberEntity(detail.getId(), currentUser);
            var role = Role.builder().name(detail.getRoleName()).workspace(workspace).build();
            roleRepository.save(role);
            if(employee.equals(manager)){
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
            }
            if (!employee.getWorkspace().equals(manager.getWorkspace())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
            }
            var employeeManager = EmployeeManager.builder()
                    .employee(employee.getEmployee())
                    .manager(manager.getManager())
                    .role(role)
                    .build();

            employeeManagerRepository.save(employeeManager);
        }
    }

    @Transactional
    public void modifyEmployeeManager(CurrentUser currentUser, Member employeeMember, MemberRequest request) {
        if(employeeMember.getType() != MemberType.employee){
            return;
        }
        var req = (EmployeeRequest)request;
        var list = req.getManagers();
        var workspace = employeeMember.getWorkspace();
        for(var detail : list) {
            var manager = getMemberEntity(detail.getId(), currentUser);
            var role = Role.builder().name(detail.getRoleName()).workspace(workspace).build();
            roleRepository.save(role);

            if (employeeMember.equals(manager)) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
            }

            var employManager = employeeManagerRepository.findByEmployeeAndManager(employeeMember.getEmployee(), manager.getManager());

            if(employManager.isEmpty()) {
                employManager = Optional.ofNullable(EmployeeManager.builder()
                        .employee(employeeMember.getEmployee())
                        .manager(manager.getManager())
                        .role(role)
                        .build());
                employeeManagerRepository.save(employManager.get());
            }


            if (!employManager.get().getEmployee().equals(employeeMember.getEmployee())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
            }
            if (!employeeMember.getWorkspace().equals(manager.getWorkspace())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
            }

            employManager.get().modifyEmployeeManager(manager.getManager(), role);
        }
    }

    @Transactional
    public boolean deleteEmployeeManager(){
        CurrentUser currentUser = new CurrentUser();

        Long employeeManagerId = new Long(1);
        Long employeeId = new Long(1);


        var employManager = employeeManagerRepository.findById(employeeManagerId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.NOT_ALLOWED_PERMISSION_ERROR));

        var employee = getMemberEntity(employeeId, currentUser);

        if(!employManager.getEmployee().equals(employee)){
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);
        }

        employeeManagerRepository.delete(employManager);

        return true;
    }
}