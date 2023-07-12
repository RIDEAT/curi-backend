package com.backend.curi.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Schema(description = "user Form")
public class UserForm{

    @Email
    @Schema(description = "email")
    private String email;


}
