package com.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseStats {
    private String courseId;
    private String courseName;
    private String image;
    private int duration;
    private Long studentCount;
}

