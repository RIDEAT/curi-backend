package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
@Document(collection = "module_contents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Content extends BaseEntity {
    @Id
    private ObjectId id;

    private Object content;

    public void modify(ModuleRequest request){
        this.content = request.getContents();
    }
}
