package com.backend.curi.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.util.Optional;

@Getter
@Setter
@Schema(description = "user Form")
public class UserRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "email")
    private String email;

    private String name;

    private Optional<String> phoneNum;

    private Optional<String> company;

    private Optional<Boolean> agreeWithMarketing;


}
