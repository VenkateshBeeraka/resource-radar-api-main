package com.resourceradar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.resourceradar.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

	Employee findByOrgEmpId(String orgEmpId);

	@Query(value = "SELECT * FROM employees WHERE first_name = ?1 AND last_name = ?2", nativeQuery = true)
	List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

	Employee findByEmail(String email);

	Optional<Employee> findByorgEmpId(String id);
	
	Employee findByOrgEmpIdOrEmail(String orgEmpId, String email);
}
