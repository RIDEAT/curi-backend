package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowSequence extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    private Sequence sequence;

    private Integer dayOffset;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_sequence_id")
    private Sequence prevSequence;

    public void modify(SequenceRequest request, Optional<Sequence> prevSequence){
        this.dayOffset = request.getDayOffset();
        if(prevSequence.isPresent())
            this.prevSequence = prevSequence.get();
    }
}
