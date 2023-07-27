package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Content;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, ObjectId> {
}
