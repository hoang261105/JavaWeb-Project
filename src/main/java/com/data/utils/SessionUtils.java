package com.data.utils;

import com.data.model.Student;
import com.data.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class SessionUtils {
    private static StudentService studentService;

    @Autowired
    public SessionUtils(StudentService studentService) {
        SessionUtils.studentService = studentService;
    }

    public static Student getLoggedInStudent(HttpServletRequest request, HttpSession session) {
        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("studentEmail".equals(cookie.getName())) {
                        String email = cookie.getValue();
                        student = studentService.findByEmail(email);
                        if (student != null) {
                            session.setAttribute("student", student);
                        }
                        break;
                    }
                }
            }
        }

        return student;
    }
}
