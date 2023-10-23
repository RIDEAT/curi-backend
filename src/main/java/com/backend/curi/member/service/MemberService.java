package com.backend.curi.member.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.MemberRepository;
import com.backend.curi.member.repository.entity.*;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final MemberRepository memberRepository;
    private final WorkspaceService workspaceService;
    private final UserworkspaceService userworkspaceService;
    private final SlackService slackService;


    public MemberResponse deleteMember(Long memberId) throws DataIntegrityViolationException {
        var member = getMember(memberId);
        memberRepository.delete(member);

        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse modifyMember(Long memberId, MemberRequest request) {
        var member = getMember(memberId);
        member.modifyInformation(request);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        var member = getMember(memberId);
        if(request.getName() != null)
            member.setName(request.getName());
        if(request.getEmail() != null)
            member.setEmail(request.getEmail());
        if(request.getPhoneNum() != null)
            member.setPhoneNum(request.getPhoneNum());
        if(request.getDepartment() != null)
            member.setDepartment(request.getDepartment());
        if(request.getStartDate() != null)
            member.setStartDate(LocalDate.parse(request.getStartDate()));
        return MemberResponse.of(member);
    }


    public List<MemberResponse> getMemberList(Long workspaceId, MemberType memberType) {
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var memberList = memberRepository.findAllByWorkspaceAndType(workspace, memberType);
        var responseList = memberList.stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
        return responseList;
    }

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        var workspace = workspaceService.getWorkspaceEntityById(request.getWid());
        var member = Member.of(request, workspace);
        slackService.sendMessageToRideat(new SlackMessageRequest("새로운 멤버를 추가했습니다. 이름 : " + member.getName() + ", 워크스페이스 id: "+ request.getWid() + ", 워크스페이스 이름: " +workspace.getName()));

        return MemberResponse.of(memberRepository.save(member));
    }

    @Transactional
    public List<MemberResponse> createMembers(List<MemberRequest> requests) {
        var workspace = workspaceService.getWorkspaceEntityById(requests.get(0).getWid());
        var members = requests.stream()
                .map(request -> Member.of(request, workspace))
                .collect(Collectors.toList());
        slackService.sendMessageToRideat(new SlackMessageRequest("csv로 새로운 멤버를 추가했습니다. 인원수 : " + members.size() + ", 워크스페이스 id: "+ requests.get(0).getWid() + ", 워크스페이스 이름: " +workspace.getName()));

        return memberRepository.saveAll(members).stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.MEMBER_NOT_EXISTS));
    }
}