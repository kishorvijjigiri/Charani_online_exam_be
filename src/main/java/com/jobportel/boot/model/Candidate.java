package com.jobportel.boot.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String phone;
    private String college;
    private String branch;
    private String gender;
    private int backlogs; // Match the frontend field

    @Lob // For storing the file in the database
    private byte[] resumeData; 
    private String resumeName;

    // Standard Getters and Setters...
}