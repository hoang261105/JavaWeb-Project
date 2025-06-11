package com.data.service.student;

import com.data.model.Student;
import com.data.repository.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
