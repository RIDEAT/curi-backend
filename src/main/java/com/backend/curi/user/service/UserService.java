package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserListResponse;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserworkspaceService userworkspaceService;
    private final WorkspaceService workspaceService;

    public User_ getUserByUserId(String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
    }

    public UserListResponse getAllUsers(Long workspaceId, CurrentUser currentUser){
        var user = getUserByUserId(currentUser.getUserId());
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        List<User_> userList = userworkspaceService.getUserListByWorkspace(workspace);

        if (!userList.contains(user)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }

        return UserListResponse.of(userList);
    }

    public UserResponse getUserResponseByUserId (String userId){
        User_ user =  userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return UserResponse.of(user);
    }
    
    public String getEmailByUserId (String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS)).getEmail();
    }

    public UserResponse updateUser(User_ user){
        User_ updatedUser = userRepository.save(user);
        UserResponse userResponse = UserResponse.of(updatedUser);
        return userResponse;
    }

    public UserResponse deleteUser(User_ user){
        UserResponse deletedUser = UserResponse.of(user);
        userRepository.delete(user);
        return deletedUser;
    }


    public UserResponse dbStore (String userId, String email) {
        User_ user = User_.builder().userId(userId).email(email).build();
        userRepository.save(user);
        UserResponse userResponse = UserResponse.of(user);
        return userResponse;
    }


}
