package com.data.repository.student;

import com.data.model.Student;

public interface StudentRepository {
    boolean save(Student student);

    boolean checkExistUserName(String userName);

    boolean checkExistEmail(String email);

    boolean checkExistPhone(String phone);

    Student checkLogin(String email, String password);
}
