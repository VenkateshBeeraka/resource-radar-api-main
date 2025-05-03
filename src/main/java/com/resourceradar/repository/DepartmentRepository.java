package com.resourceradar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.resourceradar.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

	List<Department> findByNameContainingIgnoreCase(String name);
	Page<Department> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Department> findAll(Pageable pageable);

}
