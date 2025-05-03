package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ManagerPostDTO {

	private String id;
	
	private String createdBy;

	private String modifiedBy;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

}
