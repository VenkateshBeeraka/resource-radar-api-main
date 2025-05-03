package com.resourceradar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.ManagerDto;
import com.resourceradar.entity.Employee;
import com.resourceradar.entity.EmployeeOrgRole;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public List<ManagerDto> getAllManagers() {
		List<ManagerDto> list1 = new ArrayList<ManagerDto>();
		
		List<Employee> list = employeeRepository.findAll();
		for (Employee employee : list) {
			if (employee.isActive()) {
			Set<EmployeeOrgRole> roles = employee.getRoles();

			for (EmployeeOrgRole role : roles) {
				if (role.getRole().contains("Manager") && !role.getRole().contains("ResourceManager")) {

					ManagerDto md = new ManagerDto();
					md.setId(employee.getId());
					md.setName(employee.getFirstName() + " " + employee.getLastName());

					list1.add(md);
				}
			}
		  }
		}
		return list1;
	}
}
