package com.resourceradar.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClientDTO {
    
    private String id;
    private String name;
    private String status;
    private String orgId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String clientManagerName;
}
