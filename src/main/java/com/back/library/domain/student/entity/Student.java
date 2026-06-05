package com.back.library.domain.student.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@NoArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    private String studentId;

    private String name;
    private String department;
    private String email;
    private String phoneNumber;

    public Student(String studentId, String name, String department,
                   String email, String phoneNumber) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
