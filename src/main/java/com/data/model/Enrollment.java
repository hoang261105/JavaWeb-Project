package com.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    private LocalDateTime registeredAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;
}
