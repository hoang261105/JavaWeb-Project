package com.data.controller;

import com.data.dto.LoginDTO;
import com.data.dto.StudentDTO;
import com.data.model.Student;
import com.data.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Controller
public class AuthController {
    @Autowired
    private StudentService studentService;
    
    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "register";
    }

    @GetMapping("home")
    public String home(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        model.addAttribute("student", student);
        return "home";
    }

    @GetMapping("dashboard")
    public String dashboard(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        model.addAttribute("student", student);
        model.addAttribute("viewName", "dashboard");
        model.addAttribute("fragmentName", "content");
        return "layout";
    }

    @PostMapping("register-user")
    public String registerUser(@Valid @ModelAttribute("student") StudentDTO student, BindingResult result) {
        String regexEmail = "^[a-zA-Z0-9._]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$";
        String regexPhone = "^(0([35789])\\d{8}|\\+84([35789])\\d{8})$";
        boolean isExistUserName = studentService.checkExistUserName(student.getUserName());
        boolean isExistEmail = studentService.checkExistEmail(student.getEmail());
        boolean isExistPhone = studentService.checkExistPhone(student.getPhone());
        
        if (!Pattern.matches(regexEmail, student.getEmail())) {
            result.rejectValue("email", "error.email", "Email không đúng định dạng!");
        }
        
        if (!Pattern.matches(regexPhone, student.getPhone())) {
            result.rejectValue("phone", "error.phone", "Số điện thoại không hợp lệ!");
        }
        
        if (isExistUserName){
            result.rejectValue("userName", "error.userName", "Tên người dùng đã tồn tại!");
        }

        if (isExistEmail){
            result.rejectValue("email", "error.email", "Email đã tồn tại!");
        }

        if (isExistPhone){
            result.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại!");
        }

        if (result.hasErrors()) {
            return "register";
        }

        Student student1 = new Student();
        student1.setUserName(student.getUserName());
        student1.setName(student.getName());
        student1.setDob(student.getDob());
        student1.setEmail(student.getEmail());
        student1.setPhone(student.getPhone());
        student1.setPassword(student.getPassword());
        studentService.save(student1);
        return "redirect:/login";
    }

    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("login-user")
    public String loginUser(@Valid @ModelAttribute("loginDTO") LoginDTO student, BindingResult result, HttpSession session) {
        String regexEmail = "^[a-zA-Z0-9._]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$";

        if (!Pattern.matches(regexEmail, student.getEmail())) {
            result.rejectValue("email", "error.email", "Email không đúng định dạng!");
        }

        if (result.hasErrors()) {
            return "login";
        }

        Student studentLoggedIn = studentService.checkLogin(student.getEmail(), student.getPassword());

        if (studentLoggedIn == null) {
            result.rejectValue("password", "error.password", "Email hoặc mật khâu không đúng!");
            return "login";
        }

        session.setAttribute("student", studentLoggedIn);
        return studentLoggedIn.isRole() ? "redirect:/dashboard" : "redirect:/home";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.trim().isEmpty()) {
                    setValue(LocalDate.parse(text, formatter));
                }
            }
        });
    }
}
