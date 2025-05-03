package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectDTO {
	
    private String name;
    private String type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String clientId;
    private String managerId;

}
