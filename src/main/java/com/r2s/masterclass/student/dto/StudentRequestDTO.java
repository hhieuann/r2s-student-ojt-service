package com.r2s.masterclass.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO nhận dữ liệu đăng ký OJT từ client.
 * Mọi trường bắt buộc đều được validate bằng Jakarta Bean Validation.
 */
public record StudentRequestDTO(

        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        // Đầu số di động VN: 03, 05, 07, 08, 09 + 8 chữ số = 10 số
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^0[35789]\\d{8}$", message = "Số điện thoại phải đủ 10 chữ số hợp lệ tại Việt Nam")
        String phone,

        @NotBlank(message = "Số CCCD không được để trống")
        @Pattern(regexp = "^\\d{12}$", message = "Số CCCD phải đúng 12 chữ số")
        String nationalId,

        String university,

        String major
) {
}
