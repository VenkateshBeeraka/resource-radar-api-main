package com.resourceradar.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientRequest {


    private String name;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String managerId;

}
