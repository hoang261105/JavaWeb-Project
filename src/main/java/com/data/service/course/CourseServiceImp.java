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

    @Override
    public List<Course> findCourseByName(String courseName, int page, int size) {
        return courseRepository.findCourseByName(courseName, page, size);
    }

    @Override
    public long countCourseByName(String courseName) {
        return courseRepository.countCourseByName(courseName);
    }

    @Override
    public List<Course> sortCourseById(String sortType, int page, int size) {
        return courseRepository.sortCourseById(sortType, page, size);
    }

    @Override
    public List<Course> sortCourseByName(String sortType, int page, int size) {
        return courseRepository.sortCourseByName(sortType, page, size);
    }

    @Override
    public void updateStatus(String courseId) {
        courseRepository.updateStatus(courseId);
    }

    @Override
    public List<Course> getEnrolledCoursesByStudent(int studentId, int page, int size) {
        return courseRepository.getEnrolledCoursesByStudent(studentId, page, size);
    }

    @Override
    public long countEnrolledCoursesByStudent(int studentId) {
        return courseRepository.countEnrolledCoursesByStudent(studentId);
    }
}
