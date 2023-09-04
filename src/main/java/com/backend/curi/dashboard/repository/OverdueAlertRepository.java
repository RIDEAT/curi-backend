package com.backend.curi.dashboard.repository;

import com.backend.curi.dashboard.repository.entity.OverdueAlert;
import com.backend.curi.member.repository.entity.MemberType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface OverdueAlertRepository extends MongoRepository<OverdueAlert, ObjectId> {
    List<OverdueAlert> findAllByMemberTypeAndWorkspaceId(MemberType type, Long workspaceId);
}
