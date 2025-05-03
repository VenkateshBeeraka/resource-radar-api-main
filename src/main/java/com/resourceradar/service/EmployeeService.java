package com.resourceradar.service;

import java.util.List;

import com.resourceradar.dto.DeleteStatus;
import com.resourceradar.dto.EmployeeDto;
import com.resourceradar.dto.ReportingManagerDto;
import com.resourceradar.entity.Employee;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

public interface EmployeeService {

	EmployeeDto createEmployee(EmployeeDto employeeDTO, HttpServletRequest request) throws OrgIdNotFoundException;

	List<EmployeeDto> getAllEmployees();

	List<EmployeeDto> searchEmployee(String firstname, String lastname);

	Employee getEmployeeByEmailId(String id) throws ResourceNotFoundException;

	List<ReportingManagerDto> getReportingManagers();

	DeleteStatus deleteEmployee(String id);

	DeleteStatus updateEmployee(String empId, EmployeeDto employeeDTO, HttpServletRequest request) throws ResourceNotFoundException;

	List<EmployeeDto> getSubordinates(String empid);

	void setReportingManagers(List<EmployeeDto> emps, String empId);

	boolean isValidEmployee(String employeeId);
}
