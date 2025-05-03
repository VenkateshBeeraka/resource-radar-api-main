package com.resourceradar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.resourceradar.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, String> {
	
	@Query(value = "select * from manager where client_id = ?1", nativeQuery = true)
	List<Manager> findbyClientId(String clientId);
}
