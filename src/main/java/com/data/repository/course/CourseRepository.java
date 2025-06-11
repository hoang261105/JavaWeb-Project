package com.data.repository.course;

import com.data.model.Course;
import com.data.model.Student;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll(int page, int size);

    long count();

    boolean save(Course course);

    boolean checkExistCourseId(String courseId);

    boolean checkExistCourseName(String courseName, String courseId);

    Course findById(String courseId);

    void delete(String courseId);
}
