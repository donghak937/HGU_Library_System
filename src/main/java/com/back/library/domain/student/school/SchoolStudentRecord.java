package com.back.library.domain.student.school;

public class SchoolStudentRecord {

    private final String studentId;
    private final String name;
    private final String department;
    private final String email;
    private final String phoneNumber;

    public SchoolStudentRecord(String studentId, String name, String department,
                               String email, String phoneNumber) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
