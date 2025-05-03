package com.resourceradar.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class EmployeeSkillKey implements Serializable {
    private static final long serialVersionUID = 1L;

	@Column(name = "emp_id")
    private String empId;

    @Column(name = "skill_id")
    private String skillId;

    @Column(name = "org_id")
    private String orgID;
}
