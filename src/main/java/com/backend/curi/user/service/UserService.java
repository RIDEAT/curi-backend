package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final UserworkspaceService userworkspaceService;

    public User_ getUserByUserId(String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
    }


    public List<UserResponse> getAllUsersInWorkspace(Long workspaceId, CurrentUser currentUser){
        var user = getUserByUserId(currentUser.getUserId());
        List<User_> userList = userworkspaceService.getUserListByWorkspaceId(workspaceId);

        if (!userList.contains(user)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }

        return userList.stream().map(UserResponse::of).collect(Collectors.toList());
    }

    public UserResponse getUserResponseByUserId (String userId){
        User_ user =  userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return UserResponse.of(user);
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


    public UserResponse dbStore (String userId, String name) {
        User_ user = User_.builder().userId(userId).name(name).build();
        userRepository.save(user);
        UserResponse userResponse = UserResponse.of(user);
        return userResponse;
    }


    public List<UserResponse> getUsers() {
        List<User_> userList = userRepository.findAll();
        return userList.stream().map(UserResponse::of).collect(Collectors.toList());
    }
}
