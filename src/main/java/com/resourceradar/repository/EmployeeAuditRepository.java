package com.resourceradar.repository;

import com.resourceradar.entity.EmployeeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAuditRepository extends JpaRepository<EmployeeAudit, String> {



}
