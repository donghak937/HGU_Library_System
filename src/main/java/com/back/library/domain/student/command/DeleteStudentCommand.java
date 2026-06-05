package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.service.StudentService;

public class DeleteStudentCommand implements StudentCommand {

    private final StudentService studentService;
    private final String studentId;

    public DeleteStudentCommand(StudentService studentService, String studentId) {
        this.studentService = studentService;
        this.studentId = studentId;
    }

    @Override
    public StudentCommandResponse execute() {
        try {
            studentService.deleteStudent(studentId);
            return StudentCommandResponse.success("학생 삭제가 완료되었습니다.", null);
        } catch (IllegalArgumentException e) {
            return StudentCommandResponse.failure(e.getMessage());
        }
    }
}
