package com.data.dto;

import com.data.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private int id;
    private int studentId;
    private String courseId;
    private LocalDateTime registeredAt;
    private Status status;
}
