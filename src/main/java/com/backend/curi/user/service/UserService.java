package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.user.controller.dto.UserRequest;
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

    private final SlackService slackService;
    private final UserRepository userRepository;
    private final UserworkspaceService userworkspaceService;

    public User_ getUserByUserId(String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
    }


    public UserResponse getUserResponseByUserId (String userId){
        User_ user =  userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return UserResponse.of(user);
    }
    
    public UserResponse updateUser(String userId, UserRequest userRequest){
        User_ user = getUserByUserId(userId);

        user.setName (userRequest.getName());
        if (userRequest.getPhoneNum().isPresent()) user.setPhoneNum(userRequest.getPhoneNum().get());
        if (userRequest.getCompany().isPresent()) user.setCompany(userRequest.getCompany().get());
        if (userRequest.getAgreeWithMarketing().isPresent()) user.setAgreeWithMarketing(userRequest.getAgreeWithMarketing().get());

        User_ updatedUser = userRepository.save(user);
        UserResponse userResponse = UserResponse.of(updatedUser);

        if (userRequest.getPhoneNum().isPresent() && userRequest.getCompany().isPresent()){
            slackService.sendMessageToRideat(new SlackMessageRequest("유저 세부 정보가 추가되었습니다. 이름 : " + userRequest.getName() + ", 이메일 : " + userId + ", 전화번호 : " + userRequest.getPhoneNum().get() + ", 회사 : " + userRequest.getCompany().get()));
        }

        return userResponse;
    }

    public UserResponse deleteUser(User_ user){
        UserResponse deletedUser = UserResponse.of(user);
        userRepository.delete(user);
        return deletedUser;
    }


    public UserResponse dbStore (String userId, String name) {
        var existingUser = userRepository.findByUserId(userId);
        if (existingUser.isPresent()){
            return UserResponse.of(existingUser.get());
        }

        slackService.sendMessageToRideat(new SlackMessageRequest("새로운 유저가 가입했습니다. 이름 : " + name + ", 이메일 : " + userId));
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
