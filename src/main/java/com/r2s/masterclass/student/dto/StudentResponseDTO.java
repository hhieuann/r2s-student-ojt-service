package com.r2s.masterclass.student.dto;

import com.r2s.masterclass.student.entity.StudentEntity;
import com.r2s.masterclass.student.entity.StudentStatus;

import java.util.UUID;

/**
 * DTO trả về client. Dữ liệu PII (SĐT, CCCD) luôn được che mờ,
 * không có trường nào chứa giá trị gốc.
 */
public record StudentResponseDTO(
        UUID id,
        String fullName,
        String email,
        String maskedPhone,
        String maskedNationalId,
        String university,
        String major,
        StudentStatus status
) {

    /** Chuyển Entity nội bộ sang DTO, masking PII ngay tại đây. */
    public static StudentResponseDTO fromEntity(StudentEntity entity) {
        return new StudentResponseDTO(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                maskPhone(entity.getPhone()),
                maskNationalId(entity.getNationalId()),
                entity.getUniversity(),
                entity.getMajor(),
                entity.getStatus()
        );
    }

    /** 0901234567 -> 090*****67 (giữ 3 số đầu, 2 số cuối). */
    public static String maskPhone(String phone) {
        return mask(phone, 3, 2);
    }

    /** 079123456789 -> 079******789 (giữ 3 số đầu, 3 số cuối). */
    public static String maskNationalId(String nationalId) {
        return mask(nationalId, 3, 3);
    }

    private static String mask(String value, int keepStart, int keepEnd) {
        if (value == null) {
            return null;
        }
        // Chuỗi quá ngắn thì che toàn bộ để tránh lộ dữ liệu
        if (value.length() <= keepStart + keepEnd) {
            return "*".repeat(value.length());
        }
        return value.substring(0, keepStart)
                + "*".repeat(value.length() - keepStart - keepEnd)
                + value.substring(value.length() - keepEnd);
    }
}
