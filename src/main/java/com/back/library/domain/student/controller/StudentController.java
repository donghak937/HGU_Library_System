package com.back.library.domain.student.controller;

import com.back.library.domain.student.command.AddStudentCommand;
import com.back.library.domain.student.command.DeleteStudentCommand;
import com.back.library.domain.student.command.ModifyStudentCommand;
import com.back.library.domain.student.command.StudentCommand;
import com.back.library.domain.student.command.ViewStudentCommand;
import com.back.library.domain.student.command.ViewStudentsCommand;
import com.back.library.domain.student.dto.request.StudentRequest;
import com.back.library.domain.student.dto.response.StudentCommandResponse;
import com.back.library.domain.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/StudentManagementUI")
    public String showStudentManagementUI() {
        return "student/StudentManagementUI";
    }

    @PostMapping("/addStudent")
    @ResponseBody
    public StudentCommandResponse addStudent(@RequestBody StudentRequest request) {
        StudentCommand command = new AddStudentCommand(studentService, request);
        return command.execute();
    }

    @DeleteMapping("/deleteStudent")
    @ResponseBody
    public StudentCommandResponse deleteStudent(@RequestParam String studentId) {
        StudentCommand command = new DeleteStudentCommand(studentService, studentId);
        return command.execute();
    }

    @PostMapping("/modifyStudent/{studentId}")
    @ResponseBody
    public StudentCommandResponse modifyStudent(@PathVariable String studentId,
                                                @RequestBody StudentRequest request) {
        StudentCommand command = new ModifyStudentCommand(studentService, studentId, request);
        return command.execute();
    }

    @GetMapping("/viewStudent")
    @ResponseBody
    public StudentCommandResponse viewStudent(@RequestParam String studentId) {
        StudentCommand command = new ViewStudentCommand(studentService, studentId);
        return command.execute();
    }

    @GetMapping("/viewStudents")
    @ResponseBody
    public StudentCommandResponse viewStudents() {
        StudentCommand command = new ViewStudentsCommand(studentService);
        return command.execute();
    }
}
