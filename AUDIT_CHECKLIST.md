# CHECKLIST AUDIT CODE (DEFINITION OF DONE)

| STT | Hạng mục Quality Gate | Tiêu chuẩn (DoD) | Bằng chứng | Trạng thái |
|---|---|---|---|---|
| 1 | Red-Zone Security | Có lá chắn chặn AI đọc file cấu hình nhạy cảm; 0% rò rỉ Secrets/YML | `.claude/settings.json` (`permissions.deny`) — thay cho `.copilotignore` | [x] PASSED |
| 2 | Clean Architecture | Controller chỉ nhận/trả DTO Records; 0% `@Entity` rò ra Presentation | `StudentController` chỉ dùng `StudentRequestDTO` / `StudentResponseDTO` | [x] PASSED |
| 3 | PII Masking | CCCD và SĐT được che mờ tự động tại ResponseDTO | `StudentResponseDTO.fromEntity()` → `090*****67`, `079******789`; `StudentResponseDTOTest` | [x] PASSED |
| 4 | Input Validation | Đủ `@NotBlank`, `@Email`, `@Pattern` trên RequestDTO | `StudentRequestDTO`; test 400 | [x] PASSED |
| 5 | Duplicate Business Logic | Bắt trùng Email/CCCD, ném exception → 409 | `StudentServiceImpl.register()` + unique constraint DB; test 409 | [x] PASSED |
| 6 | Exception Handling | `@RestControllerAdvice` bắt lỗi, ẩn 100% SQL Stacktrace | `GlobalExceptionHandler` + `server.error.include-stacktrace: never` | [x] PASSED |
| 7 | Prompt Evidence | Có `Dev_Prompt_Library.md` kèm Prompt 5 thành tố | `Dev_Prompt_Library.md` ở root | [x] PASSED |
