package com.resourceradar.repository;

import com.resourceradar.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, String> {
    List<Allocation> findByEmployeeId(String employeeId);

    List<Allocation> findByProjectId(String projectId);

    Allocation findAllocationByEmployeeIdAndProjectId(String employeeId, String projectId);
}
