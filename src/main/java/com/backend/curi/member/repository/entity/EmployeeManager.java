package com.backend.curi.member.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class EmployeeManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private String relation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeManager em = (EmployeeManager) o;
        return Objects.equals(id, em.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void modifyEmployeeManager(Manager manager, String relation) {
        this.manager = manager;
        this.relation = relation;
    }
}
