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
}
