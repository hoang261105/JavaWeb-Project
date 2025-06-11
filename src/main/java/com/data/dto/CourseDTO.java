package com.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    @NotBlank(message = "Mã khóa học không được để trống!")
    private String courseId;

    @NotBlank(message = "Tên khóa học không được để trống!")
    private String courseName;

    private int duration;

    @NotBlank(message = "Giảng viên không được để trống!")
    private String instructor;
    private LocalDateTime createdAt;
    private transient MultipartFile file;
    private String image;
}
