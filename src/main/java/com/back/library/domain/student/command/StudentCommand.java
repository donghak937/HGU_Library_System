package com.back.library.domain.student.command;

import com.back.library.domain.student.dto.response.StudentCommandResponse;

public interface StudentCommand {
    StudentCommandResponse execute();
}
