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
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
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
    private final RoleService roleService;
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
    public MemberResponse createMember(CurrentUser currentUser, MemberType type, MemberRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(request.getWid());
        userworkspaceService.checkAuthentication(currentUser, workspace);


        var memberBuilder = Member.of(request).type(type).workspace(workspace);
        setMemberDetail(memberBuilder, type, request);
        var member = memberBuilder.build();
        memberRepository.save(member);
        setEmployeeManager(currentUser, member, request);

        return MemberResponse.of(member);
    }

    private void setMemberDetail(Member.MemberBuilder memberbuilder, MemberType type, MemberRequest request) {
        if (type == MemberType.employee) {
            var employee = Employee.of(request).build();
            employeeRepository.save(employee);
            memberbuilder.employee(employee);
        }
        else if (type == MemberType.manager) {
            var manager = Manager.of(request).build();
            managerRepository.save(manager);
            memberbuilder.manager(manager);
        }
    }

    public Member getMemberEntity(Long id, CurrentUser currentUser) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
        var workspace = member.getWorkspace();

        userworkspaceService.checkAuthentication(currentUser, workspace);

        return member;
    }


    @Transactional
    public void setEmployeeManager(CurrentUser currentUser, Member member, MemberRequest request) {
        if(member.getType() != MemberType.employee){
            return;
        }
        var req = (EmployeeRequest)request;
        var managerList = req.getManagers();
        var workspace = member.getWorkspace();

        for(var info : managerList){
            var manager = getMemberEntity(info.getId(), currentUser);
            var role = roleService.getRoleEntity(info.getRoleId());
            //var role = Role.builder().name(info.getRoleName()).workspace(workspace).build();
            //roleRepository.save(role);

            if(member.equals(manager)){
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_REQUEST_ERROR);
            }
            if (!member.getWorkspace().equals(manager.getWorkspace())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.EMPLOYEE_AND_MANAGER_NOT_IN_SAME_WORKSPACE);

            }

            var employeeManager = EmployeeManager.builder()
                    .employee(member.getEmployee())
                    .manager(manager.getManager())
                    .role(role)
                    .build();

            employeeManagerRepository.save(employeeManager);
            member.getEmployee().getEmployeeManagers().add(employeeManager);
        }
    }

    @Transactional
    public void modifyEmployeeManager(CurrentUser currentUser, Member member, MemberRequest request) {
        if(member.getType() != MemberType.employee){
            return;
        }
        var req = (EmployeeRequest)request;
        var managerList = req.getManagers();
        var workspace = member.getWorkspace();
        for(var info : managerList) {
            var manager = getMemberEntity(info.getId(), currentUser);
            var role = roleService.getRoleEntity(info.getRoleId());

           // var role = Role.builder().name(info.getRoleName()).workspace(workspace).build();
           // roleRepository.save(role);

            if (member.equals(manager)) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_REQUEST_ERROR);

            }

            var employManager = employeeManagerRepository.findByEmployeeAndManager(member.getEmployee(), manager.getManager());

            if(employManager.isEmpty()) {
                employManager = Optional.ofNullable(EmployeeManager.builder()
                        .employee(member.getEmployee())
                        .manager(manager.getManager())
                        .role(role)
                        .build());
                employeeManagerRepository.save(employManager.get());
            }


            if (!employManager.get().getEmployee().equals(member.getEmployee())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_REQUEST_ERROR);
            }
            if (!member.getWorkspace().equals(manager.getWorkspace())) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.EMPLOYEE_AND_MANAGER_NOT_IN_SAME_WORKSPACE);

            }

            employManager.get().modifyEmployeeManager(manager.getManager(), role);
        }
    }

    @Transactional
    public boolean deleteEmployeeManager(){
        CurrentUser currentUser = new CurrentUser();

        Long employeeManagerId = 1L;
        Long employeeId = 1L;


        var employManager = employeeManagerRepository.findById(employeeManagerId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.NOT_ALLOWED_PERMISSION_ERROR));

        var employee = getMemberEntity(employeeId, currentUser);

        if(!employManager.getEmployee().equals(employee)){
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_REQUEST_ERROR);
        }

        employeeManagerRepository.delete(employManager);

        return true;
    }

    public Member getManagerByEmployeeAndRole (Member member, Role role){
        var employeeManagers = member.getEmployeeManagers();
        var managers = employeeManagers.stream().filter(employeeManager -> employeeManager.getRole().equals(role)).map(EmployeeManager::getManager).collect(Collectors.toList());

        if (managers.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.ROLE_MEMBER_NOT_EXISTS);
        var manager = managers.get(0).getMember();

        return manager;
    }
    public Employee getEmployeeById(Long employeeId){
       return employeeRepository.findById(employeeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
    }
}