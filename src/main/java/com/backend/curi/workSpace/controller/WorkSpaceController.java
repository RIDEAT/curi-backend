package com.backend.curi.workSpace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.workSpace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;

    @GetMapping("/workSpace/{workSpaceId}")
    public String getWorkSpaceName(@PathVariable String workSpaceId){
        try {
            String workSpaceName = workSpaceService.getWorkSpaceName(workSpaceId);
            return workSpaceName;

        } catch (CuriException e){
            log.error(e.getMessage());
            return e.getMessage();
        }

    }
}
