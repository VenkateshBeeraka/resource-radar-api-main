package com.resourceradar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resourceradar.entity.ApplicationRole;

public interface ApplicationRoleRepository extends JpaRepository<ApplicationRole, String> {

	List<ApplicationRole> findByNameContainingIgnoreCase(String name);
}
