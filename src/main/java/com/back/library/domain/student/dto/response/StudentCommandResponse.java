package com.back.library.domain.student.dto.response;

import lombok.Getter;

@Getter
public class StudentCommandResponse {
    private final boolean success;
    private final String message;
    private final Object data;

    public StudentCommandResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static StudentCommandResponse success(String message, Object data) {
        return new StudentCommandResponse(true, message, data);
    }

    public static StudentCommandResponse failure(String message) {
        return new StudentCommandResponse(false, message, null);
    }
}
