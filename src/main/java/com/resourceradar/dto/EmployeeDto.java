package com.resourceradar.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EmployeeDto {
	private String id;
    private String orgEmpId;
    private String firstName;
    private String lastName;
    private String email;
    private String type;
    private String designation;
    private boolean isBillable;
    private String practice;
    private LocalDateTime expStartDate;
    private LocalDateTime fissionStartDate;
    private String contactNumber;
    private String status;

    private DepartmentDTOResponse department;
    private String gender;
    private String location;
    private String nickname;
    private boolean isActive;
    private String notes;
    
    private ReportingManagerDto reportingManager;
    
    private String createdBy;
    private LocalDateTime createdTime;
    private String modifiedBy;
    private LocalDateTime modifiedTime;

    private List<EmployeeSkillsDto> skills = new ArrayList<>();
    private List<EmployeeOrgRolesDto> roles = new ArrayList<>();

//    public boolean isReportingManagerExisted() {
//        return reportingManager != null;
//    }
}
