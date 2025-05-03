package com.resourceradar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_org_application_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOrgRole {
    @EmbeddedId
    @JsonIgnore
    private EmployeeOrgRoleKey employeeOrgRoles = new EmployeeOrgRoleKey();

    @ManyToOne
    @MapsId("empId")
    @JoinColumn(name = "emp_id")
    @JsonBackReference
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private ApplicationRole applicationRole;

    @ManyToOne
    @MapsId("orgID")
    @JoinColumn(name = "org_id")
    @JsonBackReference
    private Organization organization;

    @Column(name = "role_name")
    private String role;

}
