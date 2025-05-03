package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllocationRequest {

    private String employeeId;

    private String projectId;

    private String clientId;

    private boolean isPrimary;
    private String role;

    private String jobType;

    private boolean billable;

    private Float utilization;

    private String notes;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
    
    private boolean active;
}
