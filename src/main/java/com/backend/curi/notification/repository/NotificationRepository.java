package com.backend.curi.notification.repository;

import com.backend.curi.notification.repository.entity.Notifications;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface NotificationRepository extends MongoRepository<Notifications, ObjectId> {
    List<Notifications> findAllByWorkspaceId(Long workspaceId);
}
