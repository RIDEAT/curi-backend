package com.backend.curi.security.configuration;

import com.backend.curi.common.AuthenticationInterceptor;
import com.backend.curi.common.WorkspaceAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final WorkspaceAuthInterceptor workspaceAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(workspaceAuthInterceptor)
                .addPathPatterns("/workspaces/{workspaceId}/**");
    }
}
