package com.resourceradar.mapper;

import com.resourceradar.dto.AllocationRequest;
import com.resourceradar.dto.AllocationResponse;
import com.resourceradar.entity.Allocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface AllocationMapper {
    AllocationMapper INSTANCE = Mappers.getMapper(AllocationMapper.class);

    Allocation mapToAllocation(AllocationRequest allocationRequest);

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "client.id", target = "clientId")
    AllocationResponse mapToAllocationResponse(Allocation allocation);

    List<AllocationResponse> allocationResponseMap(List<Allocation> allocations);

}
