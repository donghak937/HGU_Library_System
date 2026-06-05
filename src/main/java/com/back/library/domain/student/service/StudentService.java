package com.back.library.domain.student.service;

import com.back.library.domain.student.dto.request.StudentRequest;
import com.back.library.domain.student.dto.response.StudentResponse;
import com.back.library.domain.student.entity.Student;
import com.back.library.domain.student.repository.StudentRepository;
import com.back.library.domain.student.school.SchoolStudentRecord;
import com.back.library.domain.student.school.SchoolSystemClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolSystemClient schoolSystemClient;

    @Transactional
    public Student addStudent(StudentRequest request) {
        validateRequired(request);
        if (studentRepository.existsById(request.getStudentId())) {
            throw new IllegalArgumentException("Student already exists.");
        }

        Student student = new Student(
                request.getStudentId(),
                request.getName(),
                request.getDepartment(),
                request.getEmail(),
                request.getPhoneNumber()
        );
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found.");
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public Student modifyStudent(String studentId, StudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        if (request.getName() != null && !request.getName().isBlank()) {
            student.setName(request.getName());
        }
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            student.setDepartment(request.getDepartment());
        }
        if (request.getEmail() != null) {
            student.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            student.setPhoneNumber(request.getPhoneNumber());
        }
        return studentRepository.save(student);
    }

    public Student viewStudent(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
    }

    public List<StudentResponse> viewStudents() {
        return studentRepository.findAll().stream()
                .map(StudentResponse::new)
                .toList();
    }

    @Transactional
    public List<StudentResponse> syncStudentData() {
        List<Student> syncedStudents = schoolSystemClient.fetchStudentData().stream()
                .map(this::upsertSchoolStudent)
                .toList();

        return syncedStudents.stream()
                .map(StudentResponse::new)
                .toList();
    }

    private Student upsertSchoolStudent(SchoolStudentRecord record) {
        Student student = studentRepository.findById(record.getStudentId())
                .orElseGet(Student::new);

        student.setStudentId(record.getStudentId());
        student.setName(record.getName());
        student.setDepartment(record.getDepartment());
        student.setEmail(record.getEmail());
        student.setPhoneNumber(record.getPhoneNumber());

        return studentRepository.save(student);
    }

    private void validateRequired(StudentRequest request) {
        if (request.getStudentId() == null || request.getStudentId().isBlank()
                || request.getName() == null || request.getName().isBlank()
                || request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("studentId, name, and department are required.");
        }
    }

}
