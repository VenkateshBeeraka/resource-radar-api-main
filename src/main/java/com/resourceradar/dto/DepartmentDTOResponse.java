package com.resourceradar.dto;

import lombok.Data;

@Data
public class DepartmentDTOResponse {
	
	private String id;

	private String name;

	private String email;

	private String type;

	private String parentDepartment;

}
