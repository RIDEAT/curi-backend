package com.backend.curi.workflow.controller.dto;

import com.backend.curi.frontoffice.controller.dto.LaunchedModuleWithContent;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.contents.NotionContent;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private Object contents;
    private ModuleType type;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


    public static ContentResponse of(Content content, Module module) {
        return ContentResponse.builder()
                .id(content.getId())
                .title(module.getName())
                .contents(content.getContent())
                .type(content.getType())
                .createdBy(content.getCreatedBy())
                .updatedBy(content.getUpdatedBy())
                .createdDate(content.getCreatedDate())
                .updatedDate(content.getUpdatedDate())
                .build();
    }

    public static ContentResponse of(Content content, LaunchedModule module) {
        return ContentResponse.builder()
                .id(content.getId())
                .title(module.getName())
                .contents(content.getContent())
                .type(content.getType())
                .createdBy(content.getCreatedBy())
                .updatedBy(content.getUpdatedBy())
                .createdDate(content.getCreatedDate())
                .updatedDate(content.getUpdatedDate())
                .build();
    }


}
