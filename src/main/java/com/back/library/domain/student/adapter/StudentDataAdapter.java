package com.back.library.domain.student.adapter;

import com.back.library.domain.student.entity.Student;
import com.back.library.domain.student.school.SchoolStudentRecord;

public interface StudentDataAdapter {

    Student toStudent(SchoolStudentRecord record);
}
