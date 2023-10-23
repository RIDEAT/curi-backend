package com.backend.curi.reports;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workflow.repository.entity.Module;
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
public class Attachments extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "moudle_id")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launched_module_id")
    private LaunchedModule launchedModule;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private Member member;

    private String resourceUrl;
    private String fileName;

    public static Attachments of(Module module, LaunchedModule launchedModule, Member member, String resourceUrl, String fileName){
        return Attachments.builder()
                .module(module)
                .launchedModule(launchedModule)
                .member(member)
                .resourceUrl(resourceUrl)
                .fileName(fileName)
                .build();
    }
}
