package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.request.StudentRequest;
import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.dto.response.StudentResponse;
import com.back.library.domain.student.service.StudentService;

public class ModifyStudentCommand implements StudentCommand {

    private final StudentService studentService;
    private final String studentId;
    private final StudentRequest request;

    public ModifyStudentCommand(StudentService studentService, String studentId, StudentRequest request) {
        this.studentService = studentService;
        this.studentId = studentId;
        this.request = request;
    }

    @Override
    public StudentCommandResponse execute() {
        try {
            return StudentCommandResponse.success(
                    "학생 정보 수정이 완료되었습니다.",
                    new StudentResponse(studentService.modifyStudent(studentId, request))
            );
        } catch (IllegalArgumentException e) {
            return StudentCommandResponse.failure(e.getMessage());
        }
    }
}
