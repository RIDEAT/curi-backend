package com.backend.curi.launched.repository.entity;

import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaunchedWorkflowManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launchedWorkflowId")
    private LaunchedWorkflow launchedWorkflow;


    public static LaunchedWorkflowManager of (LaunchedWorkflow launchedWorkflow, Role role, Member member){
        return LaunchedWorkflowManager.builder().role(role).member(member).launchedWorkflow(launchedWorkflow).build();
    }
}
