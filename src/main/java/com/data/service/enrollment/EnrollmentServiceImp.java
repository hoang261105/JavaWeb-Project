package com.data.service.enrollment;

import com.data.model.Enrollment;
import com.data.repository.enrollment.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentServiceImp implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public void registerCourse(Enrollment enrollment) {
        enrollmentRepository.registerCourse(enrollment);
    }

    @Override
    public long count() {
        return enrollmentRepository.count();
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> findAllEnrollmentById(int studentId, int page, int size) {
        return enrollmentRepository.findAllEnrollmentById(studentId, page, size);
    }

    @Override
    public long countAllEnrollment(int studentId) {
        return enrollmentRepository.countAllEnrollment(studentId);
    }

    @Override
    public List<Enrollment> searchCourseByStudentId(int studentId, String courseName, int page, int size) {
        return enrollmentRepository.searchCourseByStudentId(studentId, courseName, page, size);
    }

    @Override
    public long countCourseByStudentId(int studentId, String courseName) {
        return enrollmentRepository.countCourseByStudentId(studentId, courseName);
    }

    @Override
    public List<Enrollment> sortEnrollmentByStatus(int studentId, String sortType, int page, int size) {
        return enrollmentRepository.sortEnrollmentByStatus(studentId, sortType, page, size);
    }

    @Override
    public long countEnrollmentByStatus(int studentId, String status) {
        return enrollmentRepository.countEnrollmentByStatus(studentId, status);
    }

    @Override
    public List<Enrollment> getAllEnrollments(int page, int size) {
        return enrollmentRepository.getAllEnrollments(page, size);
    }

    @Override
    public void approval(int id) {
        enrollmentRepository.approval(id);
    }

    @Override
    public void deny(int id) {
        enrollmentRepository.deny(id);
    }

    @Override
    public void cancel(int id) {
        enrollmentRepository.cancel(id);
    }

    @Override
    public List<Enrollment> searchCourse(String name, int page, int size) {
        return enrollmentRepository.searchCourse(name, page, size);
    }

    @Override
    public long countCourseSearch(String name) {
        return enrollmentRepository.countCourseSearch(name);
    }

    @Override
    public List<Enrollment> filterByStatus(String status, int page, int size) {
        return enrollmentRepository.filterByStatus(status, page, size);
    }

    @Override
    public long countByStatus(String status) {
        return enrollmentRepository.countByStatus(status);
    }

}
