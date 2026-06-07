package com.back.library.domain.student.adapter;

import com.back.library.domain.student.entity.Student;
import com.back.library.domain.student.school.SchoolStudentRecord;
import org.springframework.stereotype.Component;

@Component
public class SchoolStudentRecordAdapter implements StudentDataAdapter {

    @Override
    public Student toStudent(SchoolStudentRecord record) {
        return new Student(
                record.getStudentId(),
                record.getName(),
                record.getDepartment(),
                record.getEmail(),
                record.getPhoneNumber()
        );
    }
}
