package com.back.library.domain.student.school;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchoolSystemClient {

    public List<SchoolStudentRecord> fetchStudentData() {
        return List.of(
                new SchoolStudentRecord("S-2024001", "user123", "Computer Science",
                        "user123@handong.ac.kr", "010-1000-1001"),
                new SchoolStudentRecord("S-2024002", "hong_gildong", "Management and Economics",
                        "hong@handong.ac.kr", "010-1000-1002"),
                new SchoolStudentRecord("S-2024003", "limit_user", "Computer Science",
                        "limit@handong.ac.kr", "010-1000-1003"),
                new SchoolStudentRecord("S-2024005", "new_school_student", "Global Leadership",
                        "newstudent@handong.ac.kr", "010-1000-1005")
        );
    }
}
