package com.resourceradar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.resourceradar.entity.EmployeeHierarchy;

public interface EmployeeHierarchyRepository extends JpaRepository<EmployeeHierarchy, String> {

	@Query(value = "select * from employee_hierarchy where designation iLIKE '%CEO%';" , nativeQuery = true)
	public EmployeeHierarchy findByDesignation(String name);

	@Query(value = "select * from employee_hierarchy where employee_id= ?1 ;" , nativeQuery = true)
	public EmployeeHierarchy findByIdEmployeeId(String id);

	List<EmployeeHierarchy> findByRightValueGreaterThan(Integer rightValue);

	public List<EmployeeHierarchy> findByLeftValueGreaterThanAndRightValueLessThan(Integer leftValue,
			Integer rightValue);

	public List<EmployeeHierarchy> findByLeftValueBetween(int deletedManagerLeftValue, int deletedManagerRightValue);

	public void deleteByEmployeeId(String id);
}
