package com.backend.curi.common.feign;

import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "scheduler", url = "${feign.scheduler.url}")
public interface SchedulerOpenFeign {

    @PostMapping(value = "/sequenceMessage", produces = "application/json")
    ResponseEntity<Void> createMessage(@RequestBody SequenceMessageRequest request);

    @DeleteMapping(value = "/sequenceMessage/{sequenceId}", produces = "application/json")
    ResponseEntity<Void> deleteMessage(@PathVariable("sequenceId") Long sequenceId);
}
