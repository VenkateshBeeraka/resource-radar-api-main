package com.resourceradar.dto;

import java.util.List;

import lombok.Data;
@Data
public class EmployeesListReportingManager {
	
	public List<EmployeeDto> employees;
	
	public ReportingManagerDto reportingManager;

}
