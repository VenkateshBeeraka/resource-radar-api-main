package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClientResponse {

	private String id;
	private String name;
	private String status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String managerId;
	private String managerName;
}
