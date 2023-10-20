package com.backend.curi.reports;

import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.workflow.repository.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentsRepository extends JpaRepository<Attachments, Long> {
    List<Attachments> findAllByModule(Module module);
    Optional<Attachments> findByLaunchedModule(LaunchedModule launchedModule);
}
