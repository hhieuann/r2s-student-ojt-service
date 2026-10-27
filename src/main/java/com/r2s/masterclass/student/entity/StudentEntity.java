package com.r2s.masterclass.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA Entity nội bộ - CHỈ dùng trong tầng Service/Repository,
 * không bao giờ được trả trực tiếp ra Controller.
 */
@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    // unique ở DB làm lớp bảo vệ thứ 2 cho duplicate check
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false, unique = true, length = 12)
    private String nationalId;

    private String university;

    private String major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.PENDING_APPROVAL;

    protected StudentEntity() {
        // JPA yêu cầu constructor không tham số
    }

    public StudentEntity(String fullName, String email, String phone, String nationalId,
                         String university, String major) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.nationalId = nationalId;
        this.university = university;
        this.major = major;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getUniversity() {
        return university;
    }

    public String getMajor() {
        return major;
    }

    public StudentStatus getStatus() {
        return status;
    }
}
