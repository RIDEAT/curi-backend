package com.backend.curi.member.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "employee")
    private Member member;

    private LocalDate startDate;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EmployeeManager> employeeManagers = new ArrayList<>();

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return (id == employee.id);
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void modify(MemberRequest request) {
        var employeeRequest = (EmployeeRequest) request;
        this.startDate = LocalDate.parse(employeeRequest.getStartDate());
    }

    public static EmployeeBuilder of(MemberRequest request) {
        return Employee.builder()
                .startDate(LocalDate.parse(((EmployeeRequest) request).getStartDate()));
    }
}

