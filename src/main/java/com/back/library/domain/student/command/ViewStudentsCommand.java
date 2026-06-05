package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.service.StudentService;

public class ViewStudentsCommand implements StudentCommand {

    private final StudentService studentService;

    public ViewStudentsCommand(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public StudentCommandResponse execute() {
        return StudentCommandResponse.success(
                "Student list loaded.",
                studentService.viewStudents()
        );
    }
}
