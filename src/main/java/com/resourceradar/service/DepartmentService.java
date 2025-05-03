package com.resourceradar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.entity.Department;
import com.resourceradar.exception.DepartmentNotFoundException;

@Service
public interface DepartmentService {

	public List<Department> getAllDepartments() throws DepartmentNotFoundException;

	public List<Department> getDepartmentListBasedOnName(String name);


	public Page<Department> getAllDepartments(Pageable pageable) throws DepartmentNotFoundException;

	public Page<Department> getDepartmentListBasedOnName(String name, Pageable pageable);

	public Page<Department> getDepartmentsByName(Pageable pageable);
}
