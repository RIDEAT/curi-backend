package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workflow.repository.entity.contents.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "module_contents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Content {
    @Id
    private ObjectId id;

    private ModuleType type;

    private Long workspaceId;
    @Setter
    private Object content;

    @CreatedDate
    private LocalDateTime createdDate;

    @Setter
    @LastModifiedDate
    private LocalDateTime updatedDate;

    private String createdBy;
    private String updatedBy;

    @Version
    private Long version;

    public void setUpdatedBy(CurrentUser currentUser) {
        this.updatedBy = currentUser.getUserId();
    }

    public static Content of(ModuleType type, CurrentUser currentUser, Long workspaceId){
        var specificContent = specificContent(type);
        return Content.builder()
                .type(type)
                .content(specificContent)
                .workspaceId(workspaceId)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .createdBy(currentUser.getUserId())
                .updatedBy(currentUser.getUserId())
                .build();
    }
    public static Content of(Content contentToCopy){
        return Content.builder()
                .type(contentToCopy.getType())
                .content(contentToCopy.getContent())
                .workspaceId(contentToCopy.getWorkspaceId())
                .createdDate(contentToCopy.getCreatedDate())
                .updatedDate(contentToCopy.getUpdatedDate())
                .createdBy(contentToCopy.getCreatedBy())
                .updatedBy(contentToCopy.getUpdatedBy())
                .build();
    }

    public static Content of(Content contentToCopy, CurrentUser currentUser, Long workspaceId){
        return Content.builder()
                .type(contentToCopy.getType())
                .content(contentToCopy.getContent())
                .workspaceId(workspaceId)
                .createdDate(contentToCopy.getCreatedDate())
                .updatedDate(contentToCopy.getUpdatedDate())
                .createdBy(currentUser.getUserId())
                .updatedBy(currentUser.getUserId())
                .build();
    }
    private static Object specificContent(ModuleType type){
        switch (type){
            case notion:
                return new NotionContent();
            case notification:
            case contents:
                return new ContentsContent();
            case survey:
            case finished:
            case slack:
            case google_docs:
                return new GoogleDocsContent();
            case google_form:
                return new GoogleFormContent();
            case google_drive:
            case youtube:
                return new YoutubeContent();
            case web_url:
                return new WebContent();
            case ox_quiz:
                return new OxQuizContent();
            case attachments:
                return new AttachmentContent();
        }

        return new DefaultContent();
    }
}


