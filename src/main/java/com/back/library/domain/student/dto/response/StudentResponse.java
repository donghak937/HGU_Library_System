package com.back.library.domain.student.dto.response;

import com.back.library.domain.student.entity.Student;
import lombok.Getter;

@Getter
public class StudentResponse {
    private final String studentId;
    private final String name;
    private final String department;
    private final String email;
    private final String phoneNumber;

    public StudentResponse(Student student) {
        this.studentId = student.getStudentId();
        this.name = student.getName();
        this.department = student.getDepartment();
        this.email = student.getEmail();
        this.phoneNumber = student.getPhoneNumber();
    }
}
