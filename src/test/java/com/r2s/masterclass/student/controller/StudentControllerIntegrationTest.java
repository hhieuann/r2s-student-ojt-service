package com.r2s.masterclass.student.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Kiểm thử các kịch bản trong API Contract (giống bộ cURL của đề bài). */
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    private static final String REGISTER_URL = "/api/v1/students/register";

    @Autowired
    private MockMvc mockMvc;

    private static String body(String email, String phone, String nationalId) {
        return """
                {"fullName":"Nguyen Van A","email":"%s","phone":"%s","nationalId":"%s",
                 "university":"Đại học Bách Khoa","major":"Công nghệ Phần mềm"}
                """.formatted(email, phone, nationalId);
    }

    @Test
    void register_success_returns201WithMaskedPii_andGetByIdReturns200() throws Exception {
        String json = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("a1@gmail.com", "0901234567", "079123456789")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.maskedPhone").value("090*****67"))
                .andExpect(jsonPath("$.maskedNationalId").value("079******789"))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                // Không được lộ giá trị gốc
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.nationalId").doesNotExist())
                .andExpect(content().string(not(containsString("079123456789"))))
                .andReturn().getResponse().getContentAsString();

        String id = JsonPath.read(json, "$.id");

        mockMvc.perform(get("/api/v1/students/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.maskedPhone").value("090*****67"));
    }

    @Test
    void register_invalidPhoneAndNationalId_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("b@gmail.com", "123", "456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dữ liệu đầu vào không hợp lệ"))
                .andExpect(jsonPath("$.errors.phone").value("Số điện thoại phải đủ 10 chữ số hợp lệ tại Việt Nam"))
                .andExpect(jsonPath("$.errors.nationalId").value("Số CCCD phải đúng 12 chữ số"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void register_phoneWith12DigitsMatchingRepeatedPrefix_isRejected() throws Exception {
        // Regex gốc trong đề "^(0[3|5|7|8|9])+\\d{8}$" cho lọt số này -> đã sửa
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("c@gmail.com", "090912345678", "079123456700")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phone").exists());
    }

    @Test
    void register_duplicateEmailOrNationalId_returns409() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("dup@gmail.com", "0901234567", "079000000001")))
                .andExpect(status().isCreated());

        // Trùng email
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("dup@gmail.com", "0901234567", "079000000002")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Số CCCD hoặc Email đã tồn tại trên hệ thống OJT"))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Trùng CCCD
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("other@gmail.com", "0901234567", "079000000001")))
                .andExpect(status().isConflict());
    }

    @Test
    void getById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/students/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy hồ sơ sinh viên với ID cung cấp"));
    }

    @Test
    void getById_invalidUuid_returns400WithoutStacktrace() throws Exception {
        mockMvc.perform(get("/api/v1/students/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("Exception"))));
    }
}
