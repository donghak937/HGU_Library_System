package com.back.library.domain.student.repository;

import com.back.library.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByNameContaining(String name);
    List<Student> findByDepartment(String department);
}
