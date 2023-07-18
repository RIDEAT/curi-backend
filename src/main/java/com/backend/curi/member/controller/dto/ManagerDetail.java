package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Manager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class ManagerDetail {

    public static ManagerDetail of(Manager manager){
        return new ManagerDetail();
    }
}
