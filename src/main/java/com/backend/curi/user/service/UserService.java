package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User_ getUserByUserId(String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
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


    public UserResponse dbStore (String userId, String email) {
        User_ user = User_.builder().userId(userId).email(email).build();
        userRepository.save(user);
        UserResponse userResponse = UserResponse.builder().id(userId).email(email).build();
        return userResponse;
    }


}
