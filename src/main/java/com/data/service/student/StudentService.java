package com.data.service.student;

import com.data.model.CourseStats;
import com.data.model.Student;

import java.util.List;

public interface StudentService {
    boolean save(Student student);

    boolean checkExistUserName(String userName);

    boolean checkExistEmail(String email);

    boolean checkExistPhone(String phone);

    Student checkLogin(String email, String password);

    List<Student> findAll(int page, int size);

    long count();

    void updateToggleStatus(int studentId);

    List<Student> searchStudent(String keyword, int page, int size);

    long countStudent(String keyword);

    List<Student> sortStudentById(String sortType, int page, int size);

    List<Student> sortStudentByName(String sortType, int page, int size);

    List<CourseStats> totalStudentOfCourse();

    void update(Student student);

    Student findByEmail(String email);

    Student findById(int studentId);

    void changePassWord(int studentId, String passWord);

    Student findByPhone(String phone);
}
