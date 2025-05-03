package com.resourceradar.mapper;

import com.resourceradar.dto.EmployeeDto;
import com.resourceradar.dto.EmployeeOrgRolesDto;
import com.resourceradar.dto.EmployeeSkillsDto;
import com.resourceradar.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    Employee mapEmployee(EmployeeDto employeeDto);

    List<EmployeeDto> mapToEmployeeListDto(List<Employee> employees);

    EmployeeDto mapToEmployeeDto(Employee employee);

    EmployeeSkillKey employeeSkillMap(String value);

    @Mapping(source = "employeeSkills.skillId", target = "id")
    EmployeeSkillsDto mapToDto(EmployeeSkill employeeSkill);

    @Mapping(source = "employeeOrgRoles.roleId", target = "id")
    @Mapping(target = "role", expression = "java(employeeOrgRole.getEmployee().getFirstName() + \" \" + employeeOrgRole.getEmployee().getLastName())")
    EmployeeOrgRolesDto mapToEmployeeOrgRolesDto(EmployeeOrgRole employeeOrgRole);

    @Named("toFullName")
    default String translateToFullName(Employee employee) {
        return employee.getFirstName() + employee.getLastName();
    }

    Department mapDepartment(String value);

    String departmentMap(Department value);
}
