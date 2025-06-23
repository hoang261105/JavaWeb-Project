package com.data.service.enrollment;

import com.data.model.Enrollment;

import java.util.List;

public interface EnrollmentService {
    void registerCourse(Enrollment enrollment);

    long count();

    List<Enrollment> findAll();

    List<Enrollment> findByStudentId(int studentId);

    List<Enrollment> findAllEnrollmentById(int studentId, int page, int size);

    long countAllEnrollment(int studentId);

    List<Enrollment> searchCourseByStudentId(int studentId, String courseName, int page, int size);

    long countCourseByStudentId(int studentId, String courseName);

    List<Enrollment> sortEnrollmentByStatus(int studentId, String sortType, int page, int size);

    long countEnrollmentByStatus(int studentId, String status);

    List<Enrollment> getAllEnrollments(int page, int size);

    void approval(int id);

    void deny(int id);

    void cancel(int id);

    List<Enrollment> searchCourse(String name, int page, int size);

    long countCourseSearch(String name);

    List<Enrollment> filterByStatus(String status, int page, int size);

    long countByStatus(String status);
}
