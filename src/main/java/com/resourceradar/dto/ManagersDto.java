package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ManagersDto {
	
private String id;
	
	private String employeeId;

    private String name;

    private  String type;

    private String createdBy;

    private String modifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
