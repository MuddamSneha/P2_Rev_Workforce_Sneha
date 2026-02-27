package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "designation")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designationId;

    @Column(nullable = false, length = 100, unique = true)
    private String designationName;

    public Designation() {}

    public Designation(Long designationId, String designationName) {
        this.designationId = designationId;
        this.designationName = designationName;
    }

    public Long getDesignationId() { return designationId; }
    public void setDesignationId(Long designationId) { this.designationId = designationId; }
    public String getDesignationName() { return designationName; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
}
