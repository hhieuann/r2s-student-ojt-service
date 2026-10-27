package com.r2s.masterclass.student.service;

import com.r2s.masterclass.student.dto.StudentRequestDTO;
import com.r2s.masterclass.student.dto.StudentResponseDTO;

import java.util.UUID;

/** Nghiệp vụ hồ sơ sinh viên OJT - chỉ giao tiếp bằng DTO. */
public interface StudentService {

    StudentResponseDTO register(StudentRequestDTO request);

    StudentResponseDTO getById(UUID id);
}
