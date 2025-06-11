package com.data.service.course;

import com.data.model.Course;
import com.data.repository.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImp implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> findAll(int page, int size) {
        return courseRepository.findAll(page, size);
    }

    @Override
    public long count() {
        return courseRepository.count();
    }

    @Override
    public boolean save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public boolean checkExistCourseId(String courseId) {
        return courseRepository.checkExistCourseId(courseId);
    }

    @Override
    public boolean checkExistCourseName(String courseName, String courseId) {
        return courseRepository.checkExistCourseName(courseName, courseId);
    }

    @Override
    public Course findById(String courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public void delete(String courseId) {
        courseRepository.delete(courseId);
    }
}
