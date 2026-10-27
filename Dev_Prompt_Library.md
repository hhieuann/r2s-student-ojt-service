# DEV PROMPT LIBRARY — BỘ SỔ TAY PROMPT 5 THÀNH TỐ CHUẨN DOANH NGHIỆP
> **Dự án:** Student OJT Management Service
> **Lập trình viên:** Nguyễn Hiếu An
> **Lớp:** 6H Masterclass — AI Agent & Code Audit (R2S Academy)
> **AI Agent sử dụng:** Claude Code

----

## 1. CẤU HÌNH LÁ CHẮN VÙNG ĐỎ (Claude Code — `.claude/settings.json`)

Claude Code không dùng `.copilotignore`. Lá chắn tương đương là `permissions.deny`,
chặn AI đọc/sửa các file cấu hình nhạy cảm:

```json
{
  "permissions": {
    "deny": [
      "Read(./src/main/resources/application*.yml)",
      "Read(./src/main/resources/application*.properties)",
      "Read(./.env)",
      "Read(./.env.*)",
      "Read(./**/*.pem)",
      "Read(./**/*.key)",
      "Read(./secrets/**)",
      "Edit(./src/main/resources/application*.yml)",
      "Edit(./src/main/resources/application*.properties)",
      "Edit(./.env)",
      "Edit(./.env.*)",
      "Edit(./**/*.pem)",
      "Edit(./**/*.key)",
      "Edit(./secrets/**)"
    ]
  }
}
```

Danh sách tương ứng với `.copilotignore` gốc của đề bài:

```gitignore
src/main/resources/application*.yml
src/main/resources/application*.properties
.env
*.pem
*.key
secrets/
```

----

## 2. PROMPT 5 THÀNH TỐ (R-C-T-C-O) TẠO REST API & PII MASKING

```text
[ROLE]
Bạn là Senior Java Architect với 10+ năm kinh nghiệm thiết kế REST API Spring Boot Enterprise và tuân thủ
nghiêm ngặt Clean Architecture.

[CONTEXT]
Tôi đang viết API Đăng ký Sinh viên OJT trên Spring Boot 3.2+, Java 21, Spring Data JPA, H2 Database và Jakarta
Validation. Package chính là com.r2s.masterclass.student.

[TASK]
Khởi tạo toàn bộ bộ mã nguồn Java cho phân hệ Student OJT Service gồm 7 file:
1. StudentRequestDTO (Java Record + Jakarta Validation Annotations)
2. StudentResponseDTO (Java Record + Logic Masking PII)
3. StudentEntity (JPA Entity với ID UUID, status mặc định PENDING_APPROVAL)
4. StudentRepository (Spring Data JPA với phương thức existsByEmail và existsByNationalId)
5. StudentService & StudentServiceImpl (Có logic kiểm tra trùng Email/CCCD)
6. StudentController (REST API với response HTTP 201 Created và 200 OK)
7. GlobalExceptionHandler (@RestControllerAdvice bắt lỗi Validation 400, Duplicate 409, Not Found 404)

[CONSTRAINT]
- CẤM TUYỆT ĐỐI trả StudentEntity ra Controller. Controller chỉ giao tiếp qua DTO Records.
- BẮT BUỘC Validate RequestDTO: fullName (@NotBlank), email (@Email), phone (@Pattern 10 số VN),
  nationalId (@Pattern 12 số).
- BẮT BUỘC Masking PII trong ResponseDTO: Phone (090*****67), NationalId (079******789).
- BẮT BUỘC kiểm tra trùng Email/CCCD trong Service, ném StudentAlreadyExistsException trả về 409 Conflict.
- Ẩn toàn bộ thông tin SQL Stacktrace khi bị lỗi.

[OUTPUT]
Mã nguồn Java sạch sẽ, đầy đủ annotation, có comment giải thích ngắn gọn bằng tiếng Việt, phân chia rõ tên file
và đường dẫn package com.r2s.masterclass.student.
```

Bản rút gọn:

```text
[ROLE]: Senior Java Architect
[CONTEXT]: Spring Boot 3.2+, Clean Architecture, Java 21 Records
[TASK]: Tạo REST API Đăng ký Sinh viên OJT kèm PII Masking
[CONSTRAINT]: CẤM lộ Entity JPA, BẮT BUỘC dùng Record DTO, Masking CCCD (079******789) và SĐT
(090*****67), Validate Jakarta, Duplicate Check 409 Conflict.
[OUTPUT]: Mã nguồn Java Spring Boot 3 chuẩn phân lớp.
```

----

## 3. NHẬN XÉT AUDIT CỦA LẬP TRÌNH VIÊN (HUMAN-IN-THE-LOOP)

- **Lỗi phát hiện khi audit:**
  1. Regex SĐT trong đề `^(0[3|5|7|8|9])+\d{8}$` sai: `[3|5|7|8|9]` chấp nhận cả ký tự `|`,
     và `(...)+` cho phép lặp tiền tố nên số 12 chữ số như `090912345678` vẫn lọt.
     → Sửa thành `^0[35789]\d{8}$` (có test chứng minh).
  2. Đề ghi `errors: [...]` (mảng) ở mục I nhưng API Contract mục V dùng object `{field: message}`.
     → Chọn theo API Contract (object).
  3. Chỉ kiểm tra `existsBy...` vẫn có thể lọt khi 2 request trùng gửi cùng lúc.
     → Thêm `unique = true` ở Entity + bắt `DataIntegrityViolationException` trả 409, ẩn SQL.
  4. Handler `Exception` chung dễ biến lỗi 404/405 của Spring thành 500.
     → Giữ nguyên status gốc cho các lỗi chuẩn của Spring MVC.
  5. ID không phải UUID trên URL → trả 400 JSON thay vì stacktrace.
- **Thao tác Refactor:** Bổ sung `StudentAlreadyExistsException` (`@ResponseStatus(HttpStatus.CONFLICT)`),
  `StudentNotFoundException`, record `ErrorResponse` dùng chung; `server.error.include-stacktrace: never`.
- **Kết quả Audit:** 9/9 test tự động pass (`mvn test`), bộ cURL nghiệm thu trả đúng 201 / 400 / 409 / 200 / 404.
  Code đạt 100% tiêu chí DoD (xem `AUDIT_CHECKLIST.md`).
