package com.resourceradar.service;

import com.resourceradar.dto.AllocationRequest;
import com.resourceradar.dto.AllocationResponse;
import com.resourceradar.exception.EmployeeAllocationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AllocationService {

    AllocationResponse createAllocation(AllocationRequest allocationRequest, String orgId) throws EmployeeAllocationNotFoundException;

    AllocationResponse updateAllocation(String id, AllocationRequest allocation)
            throws EmployeeAllocationNotFoundException;

    List<AllocationResponse> getAllAllocations();

    AllocationResponse getAllocationById(String id);

    List<AllocationResponse> getAllocationByEmployeeId(String employeeId);

    List<AllocationResponse> getAllocationByProjectId(String projectId);

    String deleteEmployeeAllocation(String id) throws EmployeeAllocationNotFoundException;

    boolean isDuplicateAllocation(String employeeId, String projectId);
}
