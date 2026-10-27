package com.r2s.masterclass.student.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentResponseDTOTest {

    @Test
    void maskPhone_keepsFirst3AndLast2() {
        assertThat(StudentResponseDTO.maskPhone("0901234567")).isEqualTo("090*****67");
    }

    @Test
    void maskNationalId_keepsFirst3AndLast3() {
        assertThat(StudentResponseDTO.maskNationalId("079123456789")).isEqualTo("079******789");
    }

    @Test
    void mask_handlesNullAndShortValues() {
        assertThat(StudentResponseDTO.maskPhone(null)).isNull();
        assertThat(StudentResponseDTO.maskPhone("123")).isEqualTo("***");
    }
}
