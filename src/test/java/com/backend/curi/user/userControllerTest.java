package com.backend.curi.user;

import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.UserController;
import com.backend.curi.user.repository.RefreshTokenRepository;
import com.backend.curi.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class userControllerTest {
    private UserService userService = mock(UserService.class);
    private RefreshTokenRepository refreshTokenRepository = mock (RefreshTokenRepository.class);

    private UserController userController = new UserController(userService, refreshTokenRepository);
    private static final String userId = "ji-seung";
    private static final String email = "8514199@gmail.com";


    @Test
    public void test1(){

        assert(2 == 2);
    }


    private Authentication givenAuthentication(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        return (Authentication)currentUser;
    }

}
