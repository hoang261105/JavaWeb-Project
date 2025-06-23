package com.data.controller;

import com.data.dto.StudentDTO;
import com.data.model.Student;
import com.data.service.student.StudentService;
import com.data.utils.SessionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("adminStudent")
    public String adminStudent(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               HttpServletRequest request,
                               HttpSession session,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "sortType", required = false) String sortType) {
        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) {
            return "redirect:/login";
        }

        String field = null, sort = null;
        if (sortType != null && !sortType.trim().isEmpty()) {
            String[] params = sortType.split(" ");
            field = params[0];
            sort = params[1];
        }

        int size = 5;
        List<Student> students;
        int totalPages;

        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentService.searchStudent(keyword, page, size);
            if (students.isEmpty()){
                model.addAttribute("message", "Không có kết quả phù hợp!");
            }
            totalPages = (int) Math.ceil((double) studentService.countStudent(keyword) / size);
        }else{
            students = studentService.findAll(page, size);
            if (students.isEmpty()){
                model.addAttribute("message", "Danh sách trống!");
            }
            totalPages = (int) Math.ceil((double) studentService.count() / size);
        }

        if (field != null && sort != null) {
            switch (field) {
                case "id":
                    students = studentService.sortStudentById(sort, page, size);
                    totalPages = (int) Math.ceil((double) studentService.count() / size);
                    break;
                case "name":
                    students = studentService.sortStudentByName(sort, page, size);
                    totalPages = (int) Math.ceil((double) studentService.count() / size);
                    break;
                default:
                    students = studentService.findAll(page, size);
            }
        }

        model.addAttribute("students", students);
        model.addAttribute("pageSize", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortType", sortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("baseURL", "/adminStudent");
        model.addAttribute("viewName", "studentManagement");
        model.addAttribute("scriptName", "script");
        model.addAttribute("fragmentName", "content");
        return "layout";
    }

    @PostMapping("update-status/{id}")
    public String updateStatus(@PathVariable("id") int id){
        studentService.updateToggleStatus(id);
        return "redirect:/adminStudent";
    }

    @GetMapping("profile")
    public String profile(Model model,
                          HttpSession session,
                          HttpServletRequest request,
                          @RequestParam(defaultValue = "false") boolean isShowModal,
                          @RequestParam(value = "id", required = false) Integer id) {
        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) return "redirect:/login";

        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
        model.addAttribute("student", studentDTO);
        model.addAttribute("viewName", "profile");
        model.addAttribute("id", id);
        model.addAttribute("isShowModal", isShowModal);
        model.addAttribute("fragmentName", "content");
        return "layout_user";
    }

    @PostMapping("update-profile")
    public String updateProfile(@Valid @ModelAttribute("student") StudentDTO studentDTO,
                                BindingResult result,
                                RedirectAttributes attributes,
                                Model model,
                                HttpSession session) {

        String regexEmail = "^[a-zA-Z0-9._]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$";
        String regexPhone = "^(0([35789])\\d{8}|\\+84([35789])\\d{8})$";

        // Kiểm tra định dạng email
        if (!Pattern.matches(regexEmail, studentDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Email không hợp lệ!");
        }

        // Kiểm tra trùng email
        Student existingByEmail = studentService.findByEmail(studentDTO.getEmail());
        if (existingByEmail != null && existingByEmail.getStudentId() != studentDTO.getStudentId()) {
            result.rejectValue("email", "error.email", "Email đã tồn tại!");
        }

        // Kiểm tra định dạng số điện thoại
        if (!Pattern.matches(regexPhone, studentDTO.getPhone())) {
            result.rejectValue("phone", "error.phone", "Số điện thoại không hợp lệ!");
        }

        // Kiểm tra trùng số điện thoại
        Student existingByPhone = studentService.findByPhone(studentDTO.getPhone());
        if (existingByPhone != null && existingByPhone.getStudentId() != studentDTO.getStudentId()) {
            result.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại!");
        }

        if (result.hasErrors()) {
            model.addAttribute("student", studentDTO);
            model.addAttribute("viewName", "profile");
            model.addAttribute("fragmentName", "content");
            return "layout_user";
        }

        Student existingStudent = studentService.findById(studentDTO.getStudentId());
        if (existingStudent != null) {
            existingStudent.setName(studentDTO.getName());
            existingStudent.setEmail(studentDTO.getEmail());
            existingStudent.setPhone(studentDTO.getPhone());
            existingStudent.setSex(studentDTO.isSex());
            existingStudent.setDob(studentDTO.getDob());

            // Cập nhật vào DB
            studentService.update(existingStudent);

            // Lưu lại vào session
            session.setAttribute("student", existingStudent);
            attributes.addFlashAttribute("success", "Cập nhật thành công!");
        }

        return "redirect:/home";
    }

    @PostMapping("change-password")
    public String changePassword(Model model,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 RedirectAttributes attributes,
                                 @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                 @RequestParam(value = "newPassword", required = false) String newPassword,
                                 @RequestParam(value = "confirmPassword", required = false) String confirmPassword) {
        boolean hasErrors = false;
        Student student = SessionUtils.getLoggedInStudent(request, session);

        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            model.addAttribute("oldPasswordError", "Vui lòng nhập mật khẩu hiện tại!");
            hasErrors = true;
        }

        if (oldPassword != null && !oldPassword.trim().isEmpty() && !oldPassword.equals(student.getPassword())) {
            model.addAttribute("oldPasswordError", "Mật khẩu không đúng!");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            model.addAttribute("newPasswordError", "Vui lòng nhập mật khẩu mới!");
            hasErrors = true;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            model.addAttribute("confirmPasswordError", "Vui lòng nhập xác nhận mật khẩu!");
            hasErrors = true;
        }

        if (newPassword != null && !newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Mật khẩu không khớp!");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("isShowModal", true);
            model.addAttribute("oldPassword", oldPassword);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("confirmPassword", confirmPassword);
            model.addAttribute("viewName", "profile");
            model.addAttribute("fragmentName", "content");
            model.addAttribute("student", student);
            return "layout_user";
        }

        studentService.changePassWord(student.getStudentId(), newPassword);
        attributes.addFlashAttribute("success", "Cập nhật mật khẩu thành công!");
        model.addAttribute("isShowModal", false);
        return "redirect:/profile";
    }
}
