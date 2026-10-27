package com.r2s.masterclass.student.service;

import com.r2s.masterclass.student.dto.StudentRequestDTO;
import com.r2s.masterclass.student.dto.StudentResponseDTO;
import com.r2s.masterclass.student.entity.StudentEntity;
import com.r2s.masterclass.student.exception.StudentAlreadyExistsException;
import com.r2s.masterclass.student.exception.StudentNotFoundException;
import com.r2s.masterclass.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public StudentResponseDTO register(StudentRequestDTO request) {
        // Kiểm tra trùng cả Email và CCCD trước khi lưu
        if (studentRepository.existsByEmail(request.email())
                || studentRepository.existsByNationalId(request.nationalId())) {
            throw new StudentAlreadyExistsException();
        }

        StudentEntity entity = new StudentEntity(
                request.fullName(),
                request.email(),
                request.phone(),
                request.nationalId(),
                request.university(),
                request.major()
        );

        // saveAndFlush để lỗi unique constraint (nếu có race condition) nổ ngay trong transaction
        return StudentResponseDTO.fromEntity(studentRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getById(UUID id) {
        return studentRepository.findById(id)
                .map(StudentResponseDTO::fromEntity)
                .orElseThrow(StudentNotFoundException::new);
    }
}
