package com.data.service.student;

import com.data.model.CourseStats;
import com.data.model.Student;
import com.data.repository.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public boolean save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public boolean checkExistUserName(String userName) {
        return studentRepository.checkExistUserName(userName);
    }

    @Override
    public boolean checkExistEmail(String email) {
        return studentRepository.checkExistEmail(email);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return studentRepository.checkExistPhone(phone);
    }

    @Override
    public Student checkLogin(String email, String password) {
        return studentRepository.checkLogin(email, password);
    }

    @Override
    public List<Student> findAll(int page, int size) {
        return studentRepository.findAll(page, size);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }

    @Override
    public void updateToggleStatus(int studentId) {
        studentRepository.updateToggleStatus(studentId);
    }

    @Override
    public List<Student> searchStudent(String keyword, int page, int size) {
        return studentRepository.searchStudent(keyword, page, size);
    }

    @Override
    public long countStudent(String keyword) {
        return studentRepository.countStudent(keyword);
    }

    @Override
    public List<Student> sortStudentById(String sortType, int page, int size) {
        return studentRepository.sortStudentById(sortType, page, size);
    }

    @Override
    public List<Student> sortStudentByName(String sortType, int page, int size) {
        return studentRepository.sortStudentByName(sortType, page, size);
    }

    @Override
    public List<CourseStats> totalStudentOfCourse() {
        return studentRepository.totalStudentOfCourse();
    }

    @Override
    public void update(Student student) {
        studentRepository.update(student);
    }

    @Override
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public Student findById(int studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public void changePassWord(int studentId, String passWord) {
        studentRepository.changePassWord(studentId, passWord);
    }

    @Override
    public Student findByPhone(String phone) {
        return studentRepository.findByPhone(phone);
    }


}
