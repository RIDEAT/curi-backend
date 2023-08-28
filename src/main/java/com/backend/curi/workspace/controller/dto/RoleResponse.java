package com.backend.curi.workspace.controller.dto;

import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleResponse role = (RoleResponse) o;
        return Objects.equals(id, role.getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    public static RoleResponse of(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName());
    }
}
