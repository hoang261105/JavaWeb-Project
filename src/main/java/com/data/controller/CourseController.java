package com.data.controller;

import com.data.dto.CourseDTO;
import com.data.model.Course;
import com.data.model.Enrollment;
import com.data.model.Status;
import com.data.model.Student;
import com.data.service.CloudinaryService;
import com.data.service.course.CourseService;
import com.data.service.enrollment.EnrollmentService;
import com.data.service.student.StudentService;
import com.data.utils.SessionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class CourseController {
    @Autowired private EnrollmentService enrollmentService;
    @Autowired private CourseService courseService;
    @Autowired private CloudinaryService cloudinaryService;
    @Autowired private ModelMapper modelMapper;

    private static final int PAGE_SIZE = 5;

    private void prepareModel(Model model, List<Course> courses, int page, int totalItems, String baseURL) {
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("baseURL", baseURL);
        model.addAttribute("viewName", "courseManagement");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("scriptName", "script");
    }

    @GetMapping("home")
    public String home(Model model,
                       HttpSession session,
                       HttpServletRequest request,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       @RequestParam(defaultValue = "false") boolean isShowModal,
                       @RequestParam(value = "id", required = false) String courseId,
                       @RequestParam(defaultValue = "false") boolean isShowModalLogout) {
        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        List<Course> courses;
        int totalCourses;

        if (courseId != null){
            Course course = courseService.findById(courseId);
            model.addAttribute("course", course);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            courses = courseService.findCourseByName(keyword.trim(), page, PAGE_SIZE);
            if (courses.isEmpty()){
                model.addAttribute("message", "Không có kết quả!");
            }
            totalCourses = (int) courseService.countCourseByName(keyword.trim());
            model.addAttribute("keyword", keyword);
        } else {
            courses = courseService.getEnrolledCoursesByStudent(student.getStudentId(), page, PAGE_SIZE);
            totalCourses = (int) courseService.countEnrolledCoursesByStudent(student.getStudentId());
        }

        // Lấy danh sách tất cả enrollment của sinh viên
        List<Enrollment> enrollments = enrollmentService.findByStudentId(student.getStudentId());

        List<String> enrolledCourseIds = enrollments.stream()
                .filter(e -> e.getStatus() != Status.CANCELLED)
                .map(e -> e.getCourse().getCourseId())
                .toList();

        model.addAttribute("enrolledCourseIds", enrolledCourseIds);

        model.addAttribute("isShowModal", isShowModal);
        model.addAttribute("id", courseId);
        prepareModel(model, courses, page, totalCourses, "/home");
        model.addAttribute("viewName", "home");
        model.addAttribute("isShowModalLogout", isShowModalLogout);
        model.addAttribute("fragmentName", "content");
        return "layout_user";
    }

    @GetMapping("adminCourse")
    public String adminCourse(Model model,
                              HttpServletRequest request,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "false") boolean isShowFormAdd,
                              @RequestParam(defaultValue = "false") boolean isShowFormEdit,
                              @RequestParam(value = "id", required = false) String courseId,
                              @RequestParam(defaultValue = "false") boolean isShowModalActive,
                              HttpSession session) {
        Student student = SessionUtils.getLoggedInStudent(request, session);
        if (student == null) return "redirect:/login";

        List<Course> courses = courseService.findAll(page, PAGE_SIZE);

        if (courses.isEmpty()) model.addAttribute("message", "Danh sách trống!");

        if (isShowFormAdd) model.addAttribute("course", new CourseDTO());
        model.addAttribute("isShowFormAdd", isShowFormAdd);
        model.addAttribute("isShowFormEdit", isShowFormEdit);
        model.addAttribute("isShowModalActive", isShowModalActive);
        model.addAttribute("id", courseId);

        CourseDTO courseDTO = new CourseDTO();
        if (isShowFormEdit && courseId != null){
            Course course = courseService.findById(courseId);
            courseDTO = modelMapper.map(course, CourseDTO.class);
        }

        model.addAttribute("course", courseDTO);
        prepareModel(model, courses, page, (int) courseService.count(), "/adminCourse");
        return "layout";
    }

    @PostMapping("create-course")
    public String createCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO,
                               BindingResult result, RedirectAttributes attributes, Model model) throws IOException {
        validateCourseInput(courseDTO, result);
        if (courseDTO.getFile() == null || courseDTO.getFile().isEmpty()){
            result.rejectValue("file", "error.file", "Vui lòng chọn hình ảnh!");
        }
        if (result.hasErrors()) {
            List<Course> courses = courseService.findAll(1, PAGE_SIZE);
            prepareModel(model, courses, 1, (int) courseService.count(), "/adminCourse");
            model.addAttribute("course", courseDTO);
            model.addAttribute("isShowFormAdd", true);
            return "layout";
        }

        Course course = toCourse(courseDTO);
        course.setImage(cloudinaryService.uploadFile(courseDTO.getFile()));
        courseService.save(course);
        attributes.addFlashAttribute("success", "Thêm khóa học thành công!");
        return "redirect:/adminCourse";
    }

    @PostMapping("update-course")
    public String updateCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO,
                               BindingResult result,
                               RedirectAttributes attributes,
                               Model model) throws IOException {
        if (courseService.checkExistCourseName(courseDTO.getCourseName(), courseDTO.getCourseId())) {
            result.rejectValue("courseName", "error.name", "Tên khóa học đã tồn tại!");
        }

        if (courseDTO.getDuration() <= 0){
            result.rejectValue("duration", "error.invalid", "Thời lượng phải lớn hơn 0!");
        }

        if (result.hasErrors()){
            if (courseDTO.getImage() == null || courseDTO.getImage().isEmpty()) {
                Course oldCourse = courseService.findById(courseDTO.getCourseId());
                courseDTO.setImage(oldCourse.getImage());
            }
            List<Course> courses = courseService.findAll(1, PAGE_SIZE);
            prepareModel(model, courses, 1, (int) courseService.count(), "/adminCourse");
            model.addAttribute("course", courseDTO);
            model.addAttribute("isShowFormEdit", true);
            return "layout";
        }

        Course course = toCourse(courseDTO);
        if (courseDTO.getFile() != null && !courseDTO.getFile().isEmpty()) {
            course.setImage(cloudinaryService.uploadFile(courseDTO.getFile()));
        } else {
            course.setImage(courseService.findById(courseDTO.getCourseId()).getImage());
        }

        courseService.save(course);
        attributes.addFlashAttribute("success", "Cập nhật khóa học thành công!");
        return "redirect:/adminCourse";
    }

    @PostMapping("delete-course/{id}")
    public String deleteCourse(@PathVariable("id") String id,
                               RedirectAttributes attributes){
        List<Enrollment> enrollments = enrollmentService.findAll();

        List<Enrollment> existEnrollment = enrollments.stream()
                        .filter(enrollment -> enrollment.getCourse().getCourseId().equals(id) && enrollment.getStatus() == Status.CONFIRMED || enrollment.getStatus() == Status.WAITING)
                        .toList();

        System.out.println(existEnrollment.size());

        if (!existEnrollment.isEmpty()){
            courseService.updateStatus(id);
            attributes.addFlashAttribute("success", "Câp nhật trạng thái thành công!");
            return "redirect:/adminCourse";
        }
        courseService.delete(id);
        attributes.addFlashAttribute("success", "Xóa khóa học thành công!");
        return "redirect:/adminCourse";
    }

    @GetMapping("search")
    public String search(@RequestParam("courseName") String courseName, Model model,
                         @RequestParam(defaultValue = "1") int page) {
        List<Course> courses = courseService.findCourseByName(courseName, page, PAGE_SIZE);
        int totalItems = (int) courseService.countCourseByName(courseName);

        if (courses.isEmpty()) model.addAttribute("message", "Không có kết quả phù hợp");
        model.addAttribute("courseName", courseName);

        prepareModel(model, courses, page, totalItems, "/search");
        return "layout";
    }

    @GetMapping("sortCourse")
    public String sortCourse(@RequestParam("action") String action,
                             @RequestParam(defaultValue = "1") int page, Model model) {
        String[] parts = action.split(" ");
        String sortType = parts[0], sortField = parts[1];

        List<Course> sortedCourses;

        switch (sortField) {
            case "id":
                sortedCourses = courseService.sortCourseById(sortType, page, PAGE_SIZE);
                break;
            case "name":
                sortedCourses = courseService.sortCourseByName(sortType, page, PAGE_SIZE);
                break;
            default:
                sortedCourses = new ArrayList<>(); // empty list
        }


        model.addAttribute("action", action);
        prepareModel(model, sortedCourses, page, (int) courseService.count(), "/sortCourse");
        return "layout";
    }

    @PostMapping("openActive")
    public String openActive(@RequestParam("id") String id,
                             Model model,
                             RedirectAttributes attributes) {
        courseService.updateStatus(id);
        attributes.addFlashAttribute("success", "Câp nhật trạng thái thành công!");
        model.addAttribute("isShowModalActive", false);
        return "redirect:/adminCourse";
    }

    private void validateCourseInput(CourseDTO dto, BindingResult result) {
        if (!Pattern.matches("^(C)\\d{4}$", dto.getCourseId()) && !dto.getCourseId().isEmpty()) {
            result.rejectValue("courseId", "error", "Mã khoa học không đúng định dạng!");
        }

        if (dto.getDuration() <= 0){
            result.rejectValue("duration", "error.invalid", "Thời lượng phải lớn hơn 0!");
        }

        if (courseService.checkExistCourseId(dto.getCourseId())) {
            result.rejectValue("courseId", "error.invalid", "Mã khoa học đã tồn tại!");
        }
        if (courseService.checkExistCourseName(dto.getCourseName(), dto.getCourseId())) {
            result.rejectValue("courseName", "error.name", "Tên khóa học đã tồn tại!");
        }
    }

    private Course toCourse(CourseDTO dto) {
        Course course = new Course();
        course.setCourseId(dto.getCourseId());
        course.setCourseName(dto.getCourseName());
        course.setDuration(dto.getDuration());
        course.setInstructor(dto.getInstructor());
        return course;
    }
}
