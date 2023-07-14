package com.backend.curi.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@Schema(description = "user Form")
public class UserRequest {

    @Email
    @Schema(description = "email")
    private String email;


}
