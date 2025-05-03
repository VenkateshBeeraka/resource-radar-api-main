package com.resourceradar.repository;

import com.resourceradar.entity.AllocationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationAuditRepository extends JpaRepository<AllocationAudit, String> {
}
