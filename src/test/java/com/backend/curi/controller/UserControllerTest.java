package com.backend.curi.controller;

import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.UserController;
import com.backend.curi.user.controller.dto.UserRequest;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @Mock
    private SecurityContext securityContext;
    @MockBean
    private UserworkspaceService userworkspaceService;



    @Test
    void testRegister() throws Exception {



        String userId = "test123";
        String userEmail = "test@example.com";
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(userEmail);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);

        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);


        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .email(userEmail)
                .build();

        // Mock the userService.dbStore() method
        when(userService.dbStore(any(String.class), any(String.class)))
                .thenReturn(userResponse);

        // Perform the POST request
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}