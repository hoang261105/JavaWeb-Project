package com.data.service.course;

import com.data.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> findAll(int page, int size);

    long count();

    boolean save(Course course);

    boolean checkExistCourseId(String courseId);

    boolean checkExistCourseName(String courseName, String courseId);

    Course findById(String courseId);

    void delete(String courseId);

    List<Course> findCourseByName(String courseName, int page, int size);

    long countCourseByName(String courseName);

    List<Course> sortCourseById(String sortType, int page, int size);

    List<Course> sortCourseByName(String sortType, int page, int size);

    void updateStatus(String courseId);

    List<Course> getEnrolledCoursesByStudent(int studentId, int page, int size);

    long countEnrolledCoursesByStudent(int studentId);
}
