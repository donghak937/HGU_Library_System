package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.dto.response.StudentResponse;
import com.back.library.domain.student.service.StudentService;

public class ViewStudentCommand implements StudentCommand {

    private final StudentService studentService;
    private final String studentId;

    public ViewStudentCommand(StudentService studentService, String studentId) {
        this.studentService = studentService;
        this.studentId = studentId;
    }

    @Override
    public StudentCommandResponse execute() {
        try {
            return StudentCommandResponse.success(
                    "학생 정보 조회가 완료되었습니다.",
                    new StudentResponse(studentService.viewStudent(studentId))
            );
        } catch (IllegalArgumentException e) {
            return StudentCommandResponse.failure(e.getMessage());
        }
    }
}
