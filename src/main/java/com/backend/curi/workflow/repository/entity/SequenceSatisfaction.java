package com.backend.curi.workflow.repository.entity;


import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SequenceSatisfaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long score;

    private Boolean isScored;

    private String comment;

    @OneToOne(mappedBy = "sequenceSatisfaction", fetch = FetchType.LAZY)
    private LaunchedSequence launchedSequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memeber_id")
    private Member member;
    @Builder
    public SequenceSatisfaction(Long score, String comment, LaunchedSequence sequence, Member member, Workspace workspace, Boolean isScored){
        this.score = score;
        this.comment = comment;
        this.launchedSequence = sequence;
        this.member = member;
        this.workspace = workspace;
        this.isScored = isScored;
    }

    public static SequenceSatisfaction isNone(LaunchedSequence launchedSequence, Member member, Workspace workspace){
        return new SequenceSatisfaction(0L, "comment", launchedSequence, member, workspace, false);
    }
}
