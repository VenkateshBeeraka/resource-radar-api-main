package com.resourceradar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class Auditable {

    @JsonProperty("createdBy")
    @Column(name = "created_by")
    @JsonIgnore
    private String createdBy;

    @JsonProperty("createdTime")
    @Column(name = "created_time")
    @JsonIgnore
    private LocalDateTime createdTime;

    @JsonProperty("modifiedBy")
    @Column(name = "modified_by")
    @JsonIgnore
    private String modifiedBy;

    @JsonProperty("modifiedTime")
    @Column(name = "modified_time")
    @JsonIgnore
    private LocalDateTime modifiedTime;

}
