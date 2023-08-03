package com.backend.curi.workflow.repository;

import com.backend.curi.workflow.repository.entity.Content;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface ContentRepository extends MongoRepository<Content, ObjectId> {
}
