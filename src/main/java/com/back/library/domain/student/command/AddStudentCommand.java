package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.request.StudentRequest;
import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.dto.response.StudentResponse;
import com.back.library.domain.student.service.StudentService;

public class AddStudentCommand implements StudentCommand {

    private final StudentService studentService;
    private final StudentRequest request;

    public AddStudentCommand(StudentService studentService, StudentRequest request) {
        this.studentService = studentService;
        this.request = request;
    }

    @Override
    public StudentCommandResponse execute() {
        try {
            return StudentCommandResponse.success(
                    "학생 추가가 완료되었습니다.",
                    new StudentResponse(studentService.addStudent(request))
            );
        } catch (IllegalArgumentException e) {
            return StudentCommandResponse.failure(e.getMessage());
        }
    }
}
