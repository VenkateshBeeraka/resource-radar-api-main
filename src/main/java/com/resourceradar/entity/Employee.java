package com.resourceradar.entity;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.resourceradar.model.Auditable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "select e from Employee e")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Employee extends Auditable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "org_emp_id")
    private String orgEmpId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "type")
    private String type;

    @Column(name = "designation")
    private String designation;

    @Column(name = "billable")
    private boolean billable;

    @Column(name = "practice")
    private String practice;

    @Column(name = "exp_start_date")
    private LocalDateTime expStartDate;

    @Column(name = "fission_start_date")
    private LocalDateTime fissionStartDate;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "gender")
    private String gender;

    @Column(name = "location")
    private String location;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "active")
    private boolean active;

    @Column(name = "notes")
    private String notes;
    
    private String reportingManagerId;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "createdTime")
    private LocalDateTime createdTime;

    @Column(name = "modifiedBy")
    private String modifiedBy;

    @Column(name = "modifiedTime")
    private LocalDateTime modifiedTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EmployeeSkill> skills = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EmployeeOrgRole> roles = new HashSet<>();
}
