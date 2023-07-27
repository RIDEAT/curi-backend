package com.backend.curi.workflow.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
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
public class SequenceModule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    private Sequence sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    private Integer orderNum;

    public void modify(ModuleRequest request){
        this.orderNum = request.getOrder();
    }
}
