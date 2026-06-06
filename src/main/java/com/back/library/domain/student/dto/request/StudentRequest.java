package com.back.library.domain.student.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StudentRequest {
    private String studentId;
    private String name;
    private String department;
    private String email;
    private String phoneNumber;
}
