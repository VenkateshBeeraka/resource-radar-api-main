package com.resourceradar.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "employee_audit")
@Data
public class EmployeeAudit {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    private Employee employee;

    @Column(name = "event_type")
    private String eventType;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    private Date endDate;

    @Column(name = "org_id")
    private String orgId;

    
    // getters and setters
     
}
