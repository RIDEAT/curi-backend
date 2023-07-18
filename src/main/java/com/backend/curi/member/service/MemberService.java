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
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceService workspaceService;
    private final UserworkspaceService userworkspaceService;

    public MemberResponse getMember(CurrentUser currentUser, Long workspaceId, Long memberId){
        var member = getMemberEntity(memberId, currentUser, workspaceId);
        return MemberResponse.of(member);
    }

    public MemberResponse deleteMember(CurrentUser currentUser, Long memberId, Long workspaceId){
        var member = getMemberEntity(memberId, currentUser, workspaceId);
        memberRepository.delete(member);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse modifyMember(CurrentUser currentUser, Long memberId, MemberRequest request) {
        var member = getMemberEntity(memberId, currentUser, request.getWid());
        member.modifyInformation(request);
        return MemberResponse.of(member);
    }
    public List<MemberResponse> getMemberList (CurrentUser currentUser, Long workspaceId, MemberType memberType){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var memberList = memberRepository.findAllByWorkspaceAndType(workspace, memberType);
        var responseList = memberList.stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
        return responseList;
    }

    @Transactional
    public MemberResponse createMember (CurrentUser currentUser, MemberRequest request){

        var workspace = workspaceService.getWorkspaceEntityById(request.getWid());

        var memberBuilder = Member.of(request).workspace(workspace);

        if(request.getType() == MemberType.employee) {
            var employee = Employee.of(request).build();
            employeeRepository.save(employee);
            memberBuilder.employee(employee);
        }
//        else if (request.getType() == MemberType.employee) {
//            var manager = Manager.of(request).build();
//            managerRepository.save(manager);
//            memberBuilder.manager(manager);
//        }
        // member 가 employee 냐 manager 에 따라 추가 정보 저장해야 함.

        var member = memberBuilder.build();
        memberRepository.save(member);

        return MemberResponse.of(member);
    }

    private Member getMemberEntity(Long id, CurrentUser currentUser, Long workspaceId) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        userworkspaceService.checkAuthentication(currentUser, workspace);

        var member = memberRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
        if (!member.getWorkspace().equals(workspace))
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.NOT_ALLOWED_PERMISSION_ERROR);

        return member;
    }

}
