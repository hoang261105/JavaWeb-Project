package com.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "char_length(courseId) = 5")
public class Course {
    @Id
    @Column(columnDefinition = "char(5)")
    private String courseId;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private int duration;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String instructor;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "varchar(500)")
    private String image;

    private boolean status = true;
}
