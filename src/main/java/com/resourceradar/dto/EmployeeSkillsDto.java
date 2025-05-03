package com.resourceradar.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeSkillsDto {
    private String id;
    private String name;
    private Boolean isPrimary;
}
