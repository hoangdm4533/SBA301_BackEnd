# Hệ thống quản lý Exam

## Tóm tắt thay đổi:

### 1. Đã xóa ExamTemplate
- Xóa tất cả các file liên quan đến ExamTemplate:
  - ExamTemplate entity
  - ExamTemplateController
  - ExamTemplateService & ServiceImpl
  - ExamTemplateRepository
  - ExamTemplateResponse
  - ExamTemplate request DTOs

### 2. Tạo hệ thống quản lý Exam mới

#### Entities sử dụng:
- **Exam**: Entity chính để quản lý đề thi
- **ExamQuestion**: Liên kết giữa Exam và Question với điểm số

#### DTOs:
- **ExamRequest**: DTO để tạo/cập nhật exam
- **AddQuestionToExamRequest**: DTO để thêm câu hỏi vào exam
- **ExamResponse**: DTO response cho exam
- **ExamQuestionResponse**: DTO response cho câu hỏi trong exam

#### Repository:
- **ExamRepository**: Repository cho Exam với các phương thức tìm kiếm
- **ExamQuestionRepository**: Repository cho ExamQuestion đã được cập nhật

#### Service:
- **ExamService**: Interface định nghĩa các API quản lý exam
- **ExamServiceImpl**: Implementation với đầy đủ logic nghiệp vụ

#### Controller:
- **ExamController**: REST API endpoints cho quản lý exam

## Các API đã tạo:

### CRUD cơ bản:
- `POST /api/exams` - Tạo exam mới
- `GET /api/exams/{id}` - Lấy thông tin exam
- `GET /api/exams` - Lấy danh sách exam (có phân trang)
- `PUT /api/exams/{id}` - Cập nhật exam
- `DELETE /api/exams/{id}` - Xóa exam

### Tìm kiếm và lọc:
- `GET /api/exams/status/{status}` - Lấy exam theo trạng thái
- `GET /api/exams/search?keyword=` - Tìm kiếm exam theo từ khóa

### Quản lý câu hỏi:
- `POST /api/exams/{examId}/questions` - Thêm câu hỏi vào exam
- `DELETE /api/exams/{examId}/questions/{questionId}` - Xóa câu hỏi khỏi exam  
- `GET /api/exams/{examId}/questions` - Lấy danh sách câu hỏi trong exam

### Quản lý trạng thái:
- `PUT /api/exams/{id}/publish` - Publish exam
- `PUT /api/exams/{id}/archive` - Archive exam

## Custom Exceptions sử dụng:
- **NotFoundException**: Khi không tìm thấy exam/question
- **ConflictException**: Khi trùng lặp dữ liệu
- **BadRequestException**: Khi dữ liệu đầu vào không hợp lệ

## Security:
- Sử dụng `@SecuredEndpoint` với permission codes cho các thao tác quan trọng
- Sử dụng `@ApiResponse` và `@PageResponse` annotations

## Lưu ý:
- Không động vào hệ thống ExamTaking hiện có
- Không động vào LevelServiceImpl do Level entity khác cấu trúc
- Hệ thống hoạt động độc lập với entities hiện tại