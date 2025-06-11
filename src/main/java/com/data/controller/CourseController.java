package com.data.controller;

import com.data.dto.CourseDTO;
import com.data.model.Course;
import com.data.service.CloudinaryService;
import com.data.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("adminCourse")
    public String adminCourse(Model model, @RequestParam(defaultValue = "1") int page) {
        int size = 5;
        List<Course> courses = courseService.findAll(page, size);
        if (courses.isEmpty()) {
            model.addAttribute("message", "Danh sách trống!");
        }
        int totalPages = (int) Math.ceil((double) courseService.count() / size);
        model.addAttribute("pageSize", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("courses", courses);
        model.addAttribute("viewName", "courseManagement");
        model.addAttribute("fragmentName", "content");
        return "layout";
    }

    @GetMapping("add-course")
    public String addCourse(Model model) {
        model.addAttribute("course", new CourseDTO());
        return "add_course";
    }

    @PostMapping("create-course")
    public String createCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO,
                               BindingResult result,
                               RedirectAttributes attributes) throws IOException {
        String regexCourseId = "^(C)\\d{4}$";
        boolean isExistCourseId = courseService.checkExistCourseId(courseDTO.getCourseId());
        boolean isExistName = courseService.checkExistCourseName(courseDTO.getCourseName(), courseDTO.getCourseId());

        if (!Pattern.matches(regexCourseId, courseDTO.getCourseId()) && !courseDTO.getCourseId().isEmpty()) {
            result.rejectValue("courseId", "error", "Mã khoa học không đúng định dạng!");
        }

        if (isExistCourseId) {
            result.rejectValue("courseId", "error.invalid", "Mã khoa học đã tồn tại!");
        }

        if (isExistName) {
            result.rejectValue("courseName", "error.name", "Tên khóa học đã tồn tại!");
        }

        if (result.hasErrors()) {
            return "add_course";
        }

        String URL = cloudinaryService.uploadFile(courseDTO.getFile());
        Course course = new Course();
        course.setCourseId(courseDTO.getCourseId());
        course.setCourseName(courseDTO.getCourseName());
        course.setDuration(courseDTO.getDuration());
        course.setInstructor(courseDTO.getInstructor());
        course.setImage(URL);
        courseService.save(course);
        attributes.addFlashAttribute("success", "Thêm khóa học thành công!");
        return "redirect:/adminCourse";
    }
}
