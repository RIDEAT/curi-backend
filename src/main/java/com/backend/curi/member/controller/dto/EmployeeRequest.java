package com.backend.curi.member.controller.dto;

import com.backend.curi.exception.sequence.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest extends MemberRequest{
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "입사일 형식이 올바르지 않습니다.",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String startDate;

    List<EmployeeManagerDetail> managers;
}
