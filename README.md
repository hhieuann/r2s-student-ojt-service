# Student OJT Registration Service

Bài tập thực hành Buổi 1 — R2S Academy 6H Masterclass (AI Agent & Code Audit).

Java 21 · Spring Boot 3.5 · Spring Data JPA · Jakarta Validation · H2 In-Memory.

## Chạy

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/v1/students`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:ojtdb`, user `sa`)

## Test

```bash
mvn test
```

## Cấu trúc

```
com.r2s.masterclass.student
├── controller/StudentController.java
├── dto/StudentRequestDTO.java, StudentResponseDTO.java
├── entity/StudentEntity.java, StudentStatus.java
├── repository/StudentRepository.java
├── service/StudentService.java, StudentServiceImpl.java
└── exception/GlobalExceptionHandler.java, ErrorResponse.java,
              StudentAlreadyExistsException.java, StudentNotFoundException.java
```

## cURL nghiệm thu

> Trên Windows Git Bash, chữ tiếng Việt trong `-d '...'` có thể bị sai mã hóa → server trả
> "Body JSON không hợp lệ". Khi đó lưu body ra file UTF-8 và dùng `--data-binary @file.json`.

```bash
# 1. Register Success (201 Created)
curl -X POST http://localhost:8080/api/v1/students/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nguyen Van A","email":"nguyenvana@gmail.com","phone":"0901234567","nationalId":"079123456789","university":"Đại học Bách Khoa","major":"CNPM"}'

# 2. Validation Error (400 Bad Request - sai SĐT & CCCD)
curl -X POST http://localhost:8080/api/v1/students/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nguyen Van B","email":"nguyenvanb@gmail.com","phone":"123","nationalId":"456","university":"Đại học KHTN","major":"IT"}'

# 3. Duplicate (409 Conflict - gửi lại request 1)
curl -X POST http://localhost:8080/api/v1/students/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nguyen Van A","email":"nguyenvana@gmail.com","phone":"0901234567","nationalId":"079123456789","university":"Đại học Bách Khoa","major":"CNPM"}'

# 4. Get by ID (200 OK) - thay <id> bằng id trả về ở bước 1
curl http://localhost:8080/api/v1/students/<id>

# 5. Not Found (404)
curl http://localhost:8080/api/v1/students/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
```
