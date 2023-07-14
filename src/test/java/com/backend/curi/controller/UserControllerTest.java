package com.backend.curi.controller;

import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.UserController;
import com.backend.curi.user.controller.dto.UserRequest;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private UserworkspaceService userworkspaceService;
//    @Mock
//    private SecurityContext securityContext;
//    @Mock
//    private Authentication authentication;
//    @Mock
//    private SecurityContextHolder securityContextHolder;
//
//    private String userId = "test123";
//    private String userEmail = "test@example.com";
//    private UserRequest userRequest;
//    private CurrentUser currentUser;
//
//    @BeforeEach
//    void setup() {
//        userRequest = new UserRequest();
//        userRequest.setEmail(userEmail);
//
//        currentUser = new CurrentUser();
//        currentUser.setUserId(userId);
//    }
//
//    @Test
//    void testGetUserList() throws Exception {
//        int workspaceId = 1;
//        List<String> userIdList = Collections.singletonList(userId);
//        UserResponse userResponse = UserResponse.ofSuccess(
//                User_.builder()
//                        .userId(userId)
//                        .email(userEmail)
//                        .build());
//
//        setCurrentUser(currentUser);
//
//        when(userService.getUserResponseByUserId(userId)).thenReturn(userResponse);
//
//        // Perform the GET request
//        mockMvc.perform(get("/user/{workspaceId}", workspaceId))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.transactionId").value(11))
//                .andExpect(jsonPath("$.user_list[0].id").value(userId));
//    }
//
//    @Test
//    void testRegister() throws Exception {
//        UserResponse userResponse = UserResponse.ofSuccess(
//                User_.builder()
//                        .userId(userId)
//                        .email(userEmail)
//                        .build());
//
//        setCurrentUser(currentUser);
//
//        when(userService.dbStore(any(String.class), any(String.class)))
//                .thenReturn(userResponse);
//
//        // Perform the POST request
//        mockMvc.perform(post("/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(userRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userId))
//                .andExpect(jsonPath("$.email").value(userEmail));
//    }
//
//    @Test
//    void testUpdateUser() throws Exception {
//        User_ existingUser = new User_();
//        existingUser.setUserId(userId);
//        existingUser.setEmail(userEmail);
//
//        UserRequest updatedUserRequest = new UserRequest();
//        updatedUserRequest.setEmail("new_email@example.com");
//
//        UserResponse updatedUserResponse = UserResponse.ofSuccess(
//                User_.builder()
//                        .userId(userId)
//                        .email(updatedUserRequest.getEmail())
//                        .build());
//        setCurrentUser(currentUser);
//
//        when(userService.getUserByUserId(userId)).thenReturn(existingUser);
//        when(userService.updateUser(existingUser)).thenReturn(updatedUserResponse);
//
//        // Perform the PUT request
//        mockMvc.perform(put("/user/{userId}", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(updatedUserRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userId))
//                .andExpect(jsonPath("$.email").value(updatedUserRequest.getEmail()));
//    }
//
//    @Test
//    void testDeleteUser() throws Exception {
//        User_ existingUser = new User_();
//        existingUser.setUserId(userId);
//        existingUser.setEmail(userEmail);
//
//        UserResponse deletedUserResponse = UserResponse.ofSuccess(
//                User_.builder()
//                        .userId(userId)
//                        .email(userEmail)
//                        .build());
//
//        setCurrentUser(currentUser);
//
//        when(userService.getUserByUserId(userId)).thenReturn(existingUser);
//        when(userService.deleteUser(existingUser)).thenReturn(deletedUserResponse);
//
//        // Perform the DELETE request
//        mockMvc.perform(delete("/user/{userId}", userId))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userId))
//                .andExpect(jsonPath("$.email").value(userEmail));
//    }
//
//    @Test
//    void testUnauthorizedUserGetUserList() throws Exception {
//        int workspaceId = 1;
//
//        setCurrentUser(currentUser);
//
//
//        // Perform the GET request
//        mockMvc.perform(get("/user/{workspaceId}", workspaceId))
//                .andExpect(status().isForbidden())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.errorCode").value(ErrorType.UNAUTHORIZED_WORKSPACE.getErrorCode()));
//    }
//
//    @Test
//    void testUnauthorizedUserUpdateUser() throws Exception {
//        String unauthorizedUserId = "unauthorizedUser";
//        String userEmail = "unauthorized@example.com";
//
//        UserRequest updatedUserRequest = new UserRequest();
//        updatedUserRequest.setEmail("new_email@example.com");
//
//        setCurrentUser(currentUser);
//
//        // Perform the PUT request
//        mockMvc.perform(put("/user/{userId}", unauthorizedUserId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(updatedUserRequest)))
//                .andExpect(status().isForbidden())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.errorCode").value(ErrorType.UNAUTHORIZED_USER.getErrorCode()));
//    }
//
//    @Test
//    void testUnauthorizedUserDeleteUser() throws Exception {
//        String unauthorizedUserId = "unauthorizedUser";
//
//        setCurrentUser(currentUser);
//
//        // Perform the DELETE request
//        mockMvc.perform(delete("/user/{userId}", unauthorizedUserId))
//                .andExpect(status().isForbidden())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.errorCode").value(ErrorType.UNAUTHORIZED_USER.getErrorCode()));
//    }
//
//
//    private void setCurrentUser(CurrentUser currentUser) {
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(currentUser);
//    }
//
//    private String asJsonString(Object object) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(object);
//    }

}
