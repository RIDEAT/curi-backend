package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<User_> getAllUsers(int workspaceId, CurrentUser currentUser){
        var user = getUserByUserId(currentUser.getUserId());
        var workspace = workspaceService.getWorkspaceById(workspaceId);
        List<User_> userList = userworkspaceService.getUserListByWorkspace(workspace);

        if (!userList.contains(user)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }

        return userList;
    }
    public String getEmailByUserId (String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS)).getEmail();
    }

    public User_ updateUser(User_ user){
        User_ updatedUser = userRepository.save(user);
        return updatedUser;
    }

    public void deleteUser(User_ user){
        userRepository.delete(user);
    }


    public void dbStore (String userId, String email) {
        // 이미 유저가 있는 경우는 빼야하나?
        User_ user = User_.builder().userId(userId).email(email).build();
        userRepository.save(user);

    }


}
