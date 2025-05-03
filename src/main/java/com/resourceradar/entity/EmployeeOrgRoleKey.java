package com.resourceradar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class EmployeeOrgRoleKey implements Serializable {
    @Column(name = "emp_id")
    private String empId;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "org_id")
    private String orgID;
}
