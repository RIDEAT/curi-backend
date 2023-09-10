package com.backend.curi.user.repository.entity;

import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
public class User_ {
    @Id
    private String userId;

    private String name;

    private String phoneNum;

    private String company;

    private Boolean agreeWithMarketing;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Userworkspace> userworkspaces;

    public User_(){}

    @Builder
    public User_(String userId, String name, String phoneNum, String company, Boolean agreeWithMarketing){
        this.userId = userId;
        this.name = name;
        this.phoneNum = phoneNum;
        this.company = company;
        this.agreeWithMarketing = agreeWithMarketing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User_ user = (User_) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
