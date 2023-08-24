package com.backend.curi.common.configuration;

import com.backend.curi.common.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final WorkspaceAuthInterceptor workspaceAuthInterceptor;
    private final WorkflowAuthInterceptor workflowAuthInterceptor;
    private final SequenceAuthInterceptor sequenceAuthInterceptor;
    private final ModuleAuthInterceptor moduleAuthInterceptor;
    private final LaunchedworkflowAuthInterceptor launchedworkflowAuthInterceptor;
    private final LaunchedsequenceAuthInterceptor launchedsequenceAuthInterceptor;
    private final LaunchedmoduleAuthInterceptor launchedmoduleAuthInterceptor;
    private final FrontOfficeAuthInterceptor frontOfficeAuthInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(workspaceAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/**");

        registry.addInterceptor(workflowAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/workflows/{workflowId}/**");

        registry.addInterceptor(sequenceAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/sequences/{sequenceId}/**", "/workspaces/{workspaceId}/workflows/{workflowId}/sequences/{sequenceId}/**");

        registry.addInterceptor(moduleAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/modules/{modules}/**","/workspaces/{workspaceId}/workflows/{workflows}/sequences/{sequenceId}/modules/{moduleId}/**");


        registry.addInterceptor(launchedworkflowAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/**");

        registry.addInterceptor(launchedsequenceAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences/{sequenceId}/**");

        registry.addInterceptor(launchedmoduleAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences/{sequenceId}/modules/{moduleId}/**");

        registry.addInterceptor(frontOfficeAuthInterceptor)
                .addPathPatterns("/front-offices/**");
    }
}
