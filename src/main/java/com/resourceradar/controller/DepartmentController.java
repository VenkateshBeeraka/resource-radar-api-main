package com.resourceradar.controller;

import java.util.List;

import com.resourceradar.config.EndPointConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.entity.Department;
import com.resourceradar.exception.DepartmentNotFoundException;
import com.resourceradar.service.impl.DepartmentServiceImpl;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Tag(name = "departments")
public class DepartmentController {

	@Autowired
	private DepartmentServiceImpl departmentService;

	@GetMapping(EndPointConfig.DEPARTMENT_DETAILS)
	public ResponseEntity<List<Department>> getAllDepartment() throws DepartmentNotFoundException {
		List<Department> departments = departmentService.getAllDepartments();

		if (departments.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(departments, HttpStatus.OK);
		}
	}

    @GetMapping(EndPointConfig.DEPARTMENT_DETAILS_SEARCH)
    public ResponseEntity<List<Department>> seachDeparmentBasedOnName(@RequestParam("query") String query)  throws DepartmentNotFoundException {
        List<Department> skills = departmentService.getDepartmentListBasedOnName(query);

        if (skills.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(skills, HttpStatus.OK);
        }

    }
	@GetMapping
	public ResponseEntity<Page<Department>> getDepartmentsByName(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Department> departments = departmentService.getDepartmentsByName(pageable);
		return ResponseEntity.ok(departments);
	}

}

