package com.resourceradar.dto;

import java.util.List;

import lombok.Data;

@Data
public class DeleteStatus {
	
	private String message;
	
	private String status;
	
	private List<EmployeeDto> suboridinates;
	
	private EmployeeDto employee;

}
