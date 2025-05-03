package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectGetDTOResponse {

	private String id;
	private String name;
	private String type;
	private String status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String orgId;
	private ClientDTO client;
	private ManagersDto manager;
}
