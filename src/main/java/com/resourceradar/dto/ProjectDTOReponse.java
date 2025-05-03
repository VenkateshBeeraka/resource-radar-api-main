package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class ProjectDTOReponse {
	
	private String name;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String clientId;
    private ManagerDto manager;

}
