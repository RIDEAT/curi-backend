package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponse {
    private ObjectId id;
    private Object message;

    public static ContentResponse of(Content content) {
        return new ContentResponse(content.getId(), content.getMessage());
    }
}
