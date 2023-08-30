package com.backend.curi.frontoffice.controller.dto;

import com.backend.curi.launched.controller.dto.LaunchedModuleResponse;
import com.backend.curi.workflow.controller.dto.ContentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedModuleWithContent {

    private LaunchedModuleResponse launchedModuleResponse;
    private ContentResponse contentResponse;

    public static LaunchedModuleWithContent of (LaunchedModuleResponse launchedModuleResponse, ContentResponse contentResponse){
        return new LaunchedModuleWithContent(launchedModuleResponse, contentResponse);
    }

}
