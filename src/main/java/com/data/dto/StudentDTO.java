package com.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private int studentId;

    private String userName;

    @NotBlank(message = "Họ và tên không được để trống!")
    private String name;

    @NotNull(message = "Vui lòng chọn ngày sinh!")
    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotBlank(message = "Email không được để trống!")
    private String email;

    @NotNull(message = "Vui lòng chọn giới tính!")
    private boolean sex;

    @NotBlank(message = "Số điện thoại không được để trống!")
    private String phone;

    private String password;
}
