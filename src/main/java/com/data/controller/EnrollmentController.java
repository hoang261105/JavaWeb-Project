package com.data.controller;

import com.data.model.Course;
import com.data.model.Enrollment;
import com.data.model.Status;
import com.data.model.Student;
import com.data.service.course.CourseService;
import com.data.service.enrollment.EnrollmentService;
import com.data.service.student.StudentService;
import com.data.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EnrollmentController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private StudentService studentService;

    @PostMapping("enrollment")
    public String enrollment(@RequestParam("courseId") String courseId,
                             HttpSession session,
                             RedirectAttributes attributes,
                             Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) return "redirect:/login";

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        Course course = courseService.findById(courseId);
        enrollment.setCourse(course);
        enrollmentService.registerCourse(enrollment);

        model.addAttribute("isShowModal", false);
        attributes.addFlashAttribute("success", "Đăng ký thành công!");
        return "redirect:/home";
    }

    @GetMapping("/adminEnrollment")
    public String adminEnrollment(Model model,
                                  HttpSession session,
                                  HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(name = "id", required = false) Integer id,
                                  @RequestParam(defaultValue = "false") boolean isShowModalConfirm,
                                  @RequestParam(defaultValue = "false") boolean isShowModalDeny,
                                  @RequestParam(name = "courseName", required = false) String name,
                                  @RequestParam(name = "status", required = false) String status) {
        int size = 5;
        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) return "redirect:/login";

        List<Enrollment> enrollments;
        int totalPages;

        if (name != null && !name.trim().isEmpty()) {
            enrollments = enrollmentService.searchCourse(name, page, size);
            totalPages = (int) Math.ceil((double) enrollmentService.countCourseSearch(name) / size);
            if (enrollments.isEmpty()) model.addAttribute("message", "Không có kết quả phù hợp!");
        } else if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
            enrollments = enrollmentService.filterByStatus(status, page, size);
            totalPages = (int) Math.ceil((double) enrollmentService.countByStatus(status) / size);
            if (enrollments.isEmpty()) model.addAttribute("message", "Danh sách trống!");
        } else {
            enrollments = enrollmentService.getAllEnrollments(page, size);
            totalPages = (int) Math.ceil((double) enrollmentService.count() / size);
            if (enrollments.isEmpty()) model.addAttribute("message", "Danh sách trống!");
        }

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("isShowModalConfirm", isShowModalConfirm);
        model.addAttribute("isShowModalDeny", isShowModalDeny);
        model.addAttribute("courseName", name);
        model.addAttribute("status", status);
        model.addAttribute("id", id);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("baseURL", "/adminEnrollment");
        model.addAttribute("viewName", "enrollmentManagement");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("scriptName", "script");

        return "layout";
    }

    @GetMapping("history-enrollment/{id}")
    public String historyEnrollment(@PathVariable("id") int id,
                                    Model model,
                                    HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(value = "courseName", required = false) String name,
                                    @RequestParam(value = "action", required = false) String action,
                                    @RequestParam(defaultValue = "false") boolean isShowModalCancel,
                                    @RequestParam(value = "enrollmentId", required = false) Integer enrollmentId,
                                    HttpSession session) {
        int size = 5;
        List<Enrollment> enrollmentList;
        int totalPages;

        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) return "redirect:/login";

        if (name != null && !name.trim().isEmpty()){
            enrollmentList = enrollmentService.searchCourseByStudentId(id, name, page, size);
            if (enrollmentList.isEmpty()){
                model.addAttribute("message", "Không có kết quả phù hợp!");
            }
            totalPages = (int) Math.ceil((double) enrollmentService.countCourseByStudentId(id, name) / size);
        } else if (action != null && !action.trim().isEmpty() && !action.equals("all")) {
            enrollmentList = enrollmentService.sortEnrollmentByStatus(id, action, page, size);
            if (enrollmentList.isEmpty()){
                model.addAttribute("message", "Danh sách trống!");
            }
            totalPages = (int) Math.ceil((double) enrollmentService.countAllEnrollment(id) / size);
        } else{
            enrollmentList = enrollmentService.findAllEnrollmentById(id, page, size);
            if (enrollmentList.isEmpty()){
                model.addAttribute("message", "Danh sách trống!");
            }
            totalPages = (int) Math.ceil((double) enrollmentService.countAllEnrollment(id) / size);
        }

        model.addAttribute("id", id);
        model.addAttribute("action", action);
        model.addAttribute("currentPage", page);
        model.addAttribute("name", name);
        model.addAttribute("isShowModalCancel", isShowModalCancel);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("enrollmentId", enrollmentId);
        model.addAttribute("baseURL", "/history-enrollment/" + id);
        model.addAttribute("enrollmentList", enrollmentList);
        model.addAttribute("viewName", "history_enrollment");
        model.addAttribute("fragmentName", "content");
        return "layout_user";
    }

    @PostMapping("approval")
    public String approval(@RequestParam(name = "id", required = false) Integer id, RedirectAttributes attributes, Model model) {
        enrollmentService.approval(id);
        attributes.addFlashAttribute("success", "Duyệt đăng ký thành công!");
        model.addAttribute("isShowModalConfirm", false);
        return "redirect:/adminEnrollment";
    }

    @PostMapping("deny")
    public String deny(@RequestParam(name = "id", required = false) Integer id, RedirectAttributes attributes, Model model) {
        enrollmentService.deny(id);
        attributes.addFlashAttribute("success", "Từ chối đăng ký thành công!");
        model.addAttribute("isShowModalDeny", false);
        return "redirect:/adminEnrollment";
    }

    @PostMapping("cancel")
    public String cancel(@RequestParam(name = "id", required = false) Integer id,
                         RedirectAttributes attributes,
                         Model model,
                         HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        enrollmentService.cancel(id);
        attributes.addFlashAttribute("success", "Hủy đăng ký thành công!");
        model.addAttribute("isShowModalCancel", false);
        return "redirect:/history-enrollment/" + student.getStudentId();
    }
}
