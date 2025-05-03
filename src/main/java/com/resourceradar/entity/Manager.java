package com.resourceradar.entity;

import org.hibernate.annotations.GenericGenerator;

import com.resourceradar.model.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "manager")
@Data
public class Manager extends Auditable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(name = "emp_id")
    private String employeeId;

    @Column(name = "name")
    private String name;
    
    @Column(name = "type")
    private  String type;
    
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    @JsonIgnore
//    private Project project;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;
    
    public Manager() {
    }
    public Manager(String id) {
        this.id = id;
    }

    // Getters and Setters, constructors, and other methods
}
