package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Content;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Object contents;

    public static ContentResponse of(Content content) {
        return new ContentResponse(content.getId(), content.getContent());
    }
}
