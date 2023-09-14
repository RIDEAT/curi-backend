package com.backend.curi.member.controller.dto;

import com.backend.curi.exception.sequence.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{2,20}$",
            message = "이름 형식이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String name;

    @Pattern(regexp = "^.{4,255}$",
            message = "이메일 형식이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String phoneNum;

    @Pattern(regexp = "^[a-zA-Z가-힣]{2,20}$",
            message = "부서 이름이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String department;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "입사일 형식이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String startDate;
}
