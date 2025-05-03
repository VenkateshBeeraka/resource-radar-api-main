package com.resourceradar.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.resourceradar.model.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allocations")
@ToString
public class Allocation extends Auditable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
    private Project project;

    //@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties
    private Client client;

    @ManyToOne
    @JoinColumn(name = "org_id")
    @JsonIgnoreProperties
    private Organization organization;
    
    @Column(name = "is_primary")
    private boolean isPrimary;

    private String role;

    @Column(name = "job_type")
    private String jobType;

    private boolean billable;

    private Float utilization;

    private String notes;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    private boolean active;

}
