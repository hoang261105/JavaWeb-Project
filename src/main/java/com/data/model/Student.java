package com.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;

    @Column(columnDefinition = "varchar(50)", nullable = false, unique = true)
    private String userName;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(columnDefinition = "varchar(100)", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean sex;

    @Column(columnDefinition = "varchar(20)", nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean role = false;

    private boolean status = true;
}
