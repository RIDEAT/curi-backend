package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
@Document(collection = "module_contents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Content extends BaseEntity {
    @Id
    private ObjectId id;

    private Object message;

    public void modify(ModuleRequest request){
        this.message = request.getMessage();
    }
}
