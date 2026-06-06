package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.service.StudentService;

public class SyncStudentDataCommand implements StudentCommand {

    private final StudentService studentService;

    public SyncStudentDataCommand(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public StudentCommandResponse execute() {
        return StudentCommandResponse.success(
                "School student data synchronized.",
                studentService.syncStudentData()
        );
    }
}
