# Chức năng Soạn Đề (Level Management) - API Documentation

## Tổng quan
Hệ thống đã được bổ sung chức năng soạn đề hoàn chỉnh bao gồm quản lý Level và Exam Template với đầy đủ các API cần thiết và phân quyền chi tiết.

## 🏗️ Kiến trúc đã được tạo

### 1. Entities
- **Level**: Quản lý các cấp độ khó (Beginner, Intermediate, Advanced, etc.)
- **ExamTemplate**: Template cho việc tạo đề thi
- **ExamQuestion**: Liên kết giữa đề thi và câu hỏi với thứ tự và điểm số

### 2. DTOs
#### Request DTOs:
- `LevelRequest`: Tạo/cập nhật level
- `ExamTemplateRequest`: Tạo/cập nhật exam template
- `AddQuestionToExamRequest`: Thêm câu hỏi vào đề

#### Response DTOs:
- `LevelResponse`: Thông tin level chi tiết
- `ExamTemplateResponse`: Thông tin exam template chi tiết
- `ExamQuestionResponse`: Thông tin câu hỏi trong đề

### 3. Repositories
- `LevelRepository`: Truy vấn dữ liệu level
- `ExamTemplateRepository`: Truy vấn dữ liệu exam template
- `ExamQuestionRepository`: Truy vấn dữ liệu câu hỏi trong đề

### 4. Services & ServiceImpl
- `LevelService` & `LevelServiceImpl`: Business logic cho level
- `ExamTemplateService` & `ExamTemplateServiceImpl`: Business logic cho exam template

### 5. Controllers
- `LevelController`: REST API cho level management
- `ExamTemplateController`: REST API cho exam template management
- `GradeController`: Đã cập nhật với các annotation custom

## 🔐 Permissions đã được thêm

### Level Permissions
- `LEVEL_VIEW`: Xem danh sách level
- `LEVEL_CREATE`: Tạo level mới
- `LEVEL_UPDATE`: Cập nhật level
- `LEVEL_DELETE`: Xóa level

### Grade Permissions (đã cập nhật)
- `GRADE_VIEW`: Xem danh sách grade
- `GRADE_CREATE`: Tạo grade mới
- `GRADE_UPDATE`: Cập nhật grade
- `GRADE_DELETE`: Xóa grade

### Exam Template Permissions
- `EXAM_TEMPLATE_VIEW`: Xem danh sách exam template
- `EXAM_TEMPLATE_CREATE`: Tạo exam template mới
- `EXAM_TEMPLATE_UPDATE`: Cập nhật exam template
- `EXAM_TEMPLATE_DELETE`: Xóa exam template
- `EXAM_TEMPLATE_MANAGE_QUESTIONS`: Quản lý câu hỏi trong exam template
- `EXAM_TEMPLATE_PUBLISH`: Publish exam template
- `EXAM_TEMPLATE_APPROVE`: Approve exam template

## 🚀 API Endpoints

### Level Management APIs
- `POST /api/levels` - Tạo level mới
- `GET /api/levels` - Lấy tất cả level (có pagination)
- `GET /api/levels/{id}` - Lấy level theo ID
- `GET /api/levels/active` - Lấy level đang hoạt động
- `GET /api/levels/difficulty/{difficulty}` - Lấy level theo độ khó
- `GET /api/levels/search` - Tìm kiếm level
- `PUT /api/levels/{id}` - Cập nhật level
- `DELETE /api/levels/{id}` - Xóa level
- `PATCH /api/levels/{id}/activate` - Kích hoạt level
- `PATCH /api/levels/{id}/deactivate` - Vô hiệu hóa level

### Exam Template Management APIs
- `POST /api/exam-templates` - Tạo exam template mới
- `GET /api/exam-templates` - Lấy tất cả exam template (có pagination)
- `GET /api/exam-templates/{id}` - Lấy exam template theo ID
- `GET /api/exam-templates/level/{levelId}` - Lấy exam template theo level
- `GET /api/exam-templates/status/{status}` - Lấy exam template theo status
- `GET /api/exam-templates/search` - Tìm kiếm exam template
- `GET /api/exam-templates/search/level/{levelId}` - Tìm kiếm exam template trong level
- `PUT /api/exam-templates/{id}` - Cập nhật exam template
- `DELETE /api/exam-templates/{id}` - Xóa exam template

### Question Management in Exam Template
- `POST /api/exam-templates/{examTemplateId}/questions` - Thêm câu hỏi vào đề
- `GET /api/exam-templates/{examTemplateId}/questions` - Lấy danh sách câu hỏi trong đề
- `PUT /api/exam-templates/{examTemplateId}/questions/{questionId}` - Cập nhật câu hỏi trong đề
- `DELETE /api/exam-templates/{examTemplateId}/questions/{questionId}` - Xóa câu hỏi khỏi đề
- `PUT /api/exam-templates/{examTemplateId}/questions/reorder` - Sắp xếp lại thứ tự câu hỏi

### Status Management
- `PATCH /api/exam-templates/{id}/publish` - Publish exam template
- `PATCH /api/exam-templates/{id}/archive` - Archive exam template
- `PATCH /api/exam-templates/{id}/approve` - Approve exam template

## 🔧 Custom Annotations được sử dụng

Tất cả các controller đã sử dụng đầy đủ các custom annotation của hệ thống:

- `@SecuredEndpoint("PERMISSION_CODE")` - Phân quyền endpoint
- `@ApiResponse(message = "Success message")` - Chuẩn hóa response format
- `@UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Action description")` - Log hoạt động user
- `@PageResponse` - Chuẩn hóa response pagination
- `@Operation` - Swagger documentation
- `@Parameter` - Swagger parameter documentation
- `@Tag` - Swagger API grouping

## 🗄️ Data Initialization

Đã tạo `LevelDataInitializer` và cập nhật `MainDataInitializer` để:
- Tự động tạo 6 level mặc định (Beginner → Expert)
- Thiết lập đúng thứ tự khởi tạo dữ liệu
- Tạo permissions mới trong hệ thống

## 📚 Documentation

Đã tạo 2 file documentation chi tiết:
- `api-docs/level-api.md` - Level Management API
- `api-docs/exam-template-api.md` - Exam Template Management API

## ✅ Tính năng hoàn thành

1. **Level Management**: ✅
   - CRUD hoàn chỉnh
   - Tìm kiếm và lọc
   - Phân quyền chi tiết
   - Validation đầy đủ

2. **Exam Template Management**: ✅
   - CRUD hoàn chỉnh
   - Quản lý câu hỏi trong đề
   - Workflow: Draft → Publish → Archive
   - Approval system
   - Phân quyền chi tiết

3. **Grade Management**: ✅
   - Cập nhật với custom annotations
   - Phân quyền mới

4. **Security & Permissions**: ✅
   - 15 permissions mới
   - Tích hợp với hệ thống phân quyền hiện có
   - User activity logging

5. **Data Initialization**: ✅
   - Level data mặc định
   - Permissions mới
   - Tích hợp vào workflow khởi tạo

6. **Documentation**: ✅
   - API documentation đầy đủ
   - Error handling
   - Request/Response examples

## 🔄 Workflow sử dụng

1. **Tạo Level**: Admin tạo các cấp độ khó
2. **Tạo Exam Template**: Giáo viên tạo template đề thi cho level
3. **Thêm câu hỏi**: Thêm câu hỏi từ question bank vào template
4. **Sắp xếp**: Sắp xếp thứ tự câu hỏi và gán điểm
5. **Publish**: Publish template để sử dụng
6. **Approve**: Admin approve template (optional)

Hệ thống đã hoàn thiện chức năng soạn đề với tất cả các API cần thiết và áp dụng đúng pattern của dự án với các custom annotation đặc biệt!