package com.resourceradar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_skill_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkill {
    @EmbeddedId
    @JsonIgnore
    private EmployeeSkillKey employeeSkills = new EmployeeSkillKey();

    @ManyToOne
    @MapsId("empId")
    @JoinColumn(name = "emp_id")
    @JsonBackReference
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @MapsId("orgID")
    @JoinColumn(name = "org_id")
    @JsonBackReference
    private Organization organization;

    @Column(name = "isPrimary")
    private Boolean isPrimary;

    @Column(name = "name")
    private String name;

}
