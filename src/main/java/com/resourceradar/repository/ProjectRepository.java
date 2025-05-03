package com.resourceradar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resourceradar.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
      List<Project> findByClientId(String clientId);
}
