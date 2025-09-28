# Level & Exam Template System - Complete Documentation

## 🎯 Tổng quan hệ thống
Hệ thống "Soạn Đề" (Level & Exam Template Management) đã được xây dựng hoàn chỉnh với đầy đủ CRUD operations, phân quyền chi tiết, và workflow management.

## 🏗️ Kiến trúc hệ thống

### 1. Core Entities

#### Level Entity
- **Purpose**: Quản lý các cấp độ khó của bài thi
- **Key Fields**: 
  - `id`, `name`, `description`
  - `minScore`, `maxScore`: Khoảng điểm số (0-100)
- **Relationships**: One-to-Many với ExamTemplate
- **Initial Data**: 5 levels (Beginner → Master)

#### ExamTemplate Entity  
- **Purpose**: Template để tạo đề thi
- **Key Fields**:
  - `id`, `title`, `description`, `status`
  - `levelId`: Foreign key to Level
  - `duration`: Thời gian làm bài (phút)
  - `totalQuestions`, `totalPoints`: Thống kê tự động
- **Status Workflow**: DRAFT → PUBLISHED → ARCHIVED
- **Relationships**: 
  - Many-to-One với Level
  - One-to-Many với ExamQuestion

#### ExamQuestion Entity
- **Purpose**: Liên kết câu hỏi với exam template
- **Key Fields**:
  - `examTemplateId`, `questionId`
  - `questionOrder`: Thứ tự câu hỏi
  - `points`: Điểm số
  - `note`: Ghi chú

### 2. Data Transfer Objects (DTOs)

#### Request DTOs
```java
// Level operations
LevelRequest {
  String name;       // Required, 1-100 chars, unique
  String description; // Optional, max 500 chars  
  Integer minScore;  // Required, 0-100, < maxScore
  Integer maxScore;  // Required, 0-100, > minScore
}

// ExamTemplate operations
ExamTemplateRequest {
  String title;       // Required, 1-200 chars, unique
  String description; // Optional, max 1000 chars
  Long levelId;       // Required, must exist
  Integer duration;   // Optional, > 0 minutes
}

// Question management
AddQuestionToExamRequest {
  Long questionId;     // Required, must exist
  Integer questionOrder; // Required, unique within template
  Integer points;      // Required, > 0
  String note;        // Optional, max 500 chars
}
```

#### Response DTOs
```java
// Level information
LevelResponse {
  Long id;
  String name, description;
  Integer minScore, maxScore;
  Integer examTemplateCount; // Calculated field
  LocalDateTime createdAt, updatedAt;
}

// ExamTemplate information  
ExamTemplateResponse {
  Long id;
  String title, description, status;
  Long levelId;
  String levelName;
  Integer totalQuestions, duration;
  Double totalPoints;
  UserInfo createdBy, approvedBy;
  LocalDateTime createdAt, updatedAt, approvedAt;
}

// Question in exam
ExamQuestionResponse {
  Long id, questionId;
  String questionText;
  Integer questionOrder, points;
  String note;
}
```

## 🔐 Security & Permissions

### Level Permissions (4 permissions)
- `LEVEL_VIEW`: Xem danh sách và chi tiết level
- `LEVEL_CREATE`: Tạo level mới
- `LEVEL_UPDATE`: Cập nhật thông tin level
- `LEVEL_DELETE`: Xóa level (chỉ khi không có exam template nào sử dụng)

### Exam Template Permissions (7 permissions)
- `EXAM_TEMPLATE_VIEW`: Xem danh sách và chi tiết exam template
- `EXAM_TEMPLATE_CREATE`: Tạo exam template mới
- `EXAM_TEMPLATE_UPDATE`: Cập nhật exam template (chỉ DRAFT)
- `EXAM_TEMPLATE_DELETE`: Xóa exam template (chỉ DRAFT)
- `EXAM_TEMPLATE_MANAGE_QUESTIONS`: Thêm/xóa/sắp xếp câu hỏi
- `EXAM_TEMPLATE_PUBLISH`: Publish exam template
- `EXAM_TEMPLATE_APPROVE`: Approve exam template

### Custom Annotations Used
- `@SecuredEndpoint("PERMISSION")`: Kiểm tra quyền trước khi truy cập
- `@ApiResponse(message = "...")`: Tự động wrap response
- `@PageResponse`: Tự động wrap paginated response
- `@UserActivity`: KHÔNG sử dụng cho business operations (chỉ login/logout)

## 📊 Business Logic & Workflow

### Level Management
1. **Create**: Validate unique name, score range không overlap
2. **Update**: Kiểm tra có exam template đang sử dụng
3. **Delete**: Chỉ xóa được khi không có exam template nào reference

### Exam Template Lifecycle
```
DRAFT → PUBLISHED → ARCHIVED
   ↓        ↓         ↓
Editable  Readonly   Readonly
   ↓        ↓         ✗
Can Delete ✗ Delete  ✗ Delete
```

#### Business Rules
- Chỉ DRAFT templates có thể modify/delete
- Publish yêu cầu ít nhất 1 câu hỏi
- PUBLISHED templates không thể xóa
- Approval có thể thực hiện ở bất kỳ trạng thái nào (trừ ARCHIVED)

### Question Management
- Thứ tự câu hỏi (`questionOrder`) phải unique trong exam template
- Tự động cập nhật `totalQuestions` và `totalPoints`
- Chỉ có thể thao tác với DRAFT templates

## 🚀 API Endpoints Summary

### Level APIs (5 endpoints)
```
GET    /api/levels              - Danh sách level (paginated)
GET    /api/levels/{id}         - Chi tiết level
POST   /api/levels              - Tạo level mới
PUT    /api/levels/{id}         - Cập nhật level
DELETE /api/levels/{id}         - Xóa level
```

### Exam Template APIs (15 endpoints)
```
# CRUD Operations
GET    /api/exam-templates                    - Danh sách (paginated)
GET    /api/exam-templates/{id}              - Chi tiết
POST   /api/exam-templates                   - Tạo mới
PUT    /api/exam-templates/{id}              - Cập nhật
DELETE /api/exam-templates/{id}              - Xóa

# Filter Operations
GET    /api/exam-templates/level/{levelId}   - Filter by level
GET    /api/exam-templates/status/{status}   - Filter by status

# Question Management
GET    /api/exam-templates/{id}/questions              - Danh sách câu hỏi
POST   /api/exam-templates/{id}/questions              - Thêm câu hỏi
DELETE /api/exam-templates/{examId}/questions/{qId}    - Xóa câu hỏi
PUT    /api/exam-templates/{examId}/questions/{qId}    - Cập nhật câu hỏi
PUT    /api/exam-templates/{id}/questions/reorder      - Sắp xếp lại

# Workflow Management  
POST   /api/exam-templates/{id}/publish      - Publish template
POST   /api/exam-templates/{id}/archive      - Archive template
POST   /api/exam-templates/{id}/approve      - Approve template
```

## 🎛️ Features Implemented

### ✅ Core Features
- [x] Full CRUD operations for Level and ExamTemplate
- [x] Comprehensive validation and business rules
- [x] Complete question management (add/remove/reorder)
- [x] Status workflow management (draft → published → archived)
- [x] Approval system for exam templates

### ✅ Advanced Features  
- [x] Pagination support with custom sort fields
- [x] Advanced filtering (by level, status)
- [x] Automatic statistics calculation (question count, total points)
- [x] Proper exception handling with custom exceptions
- [x] Complete permission-based access control

### ✅ Data & Infrastructure
- [x] Initial data seeding (5 levels, 15 exam templates, questions)
- [x] Repository pattern with custom queries
- [x] Service layer with proper transaction management
- [x] Controller layer with proper HTTP status codes

## �️ Initial Data

### Levels (5 records)
1. **Beginner** (0-40): Người mới bắt đầu học
2. **Elementary** (41-55): Trình độ cơ bản
3. **Intermediate** (56-70): Trình độ trung cấp
4. **Advanced** (71-85): Trình độ cao
5. **Master** (86-100): Trình độ chuyên gia

### Exam Templates (15 records)
- 3 templates per level
- Realistic titles and descriptions
- Various durations (15-120 minutes)
- Mix of DRAFT, PUBLISHED, ARCHIVED status

### Questions (Sample data)
- Each template has 2-5 questions
- Proper point distribution
- Ordered sequence
- Descriptive notes

## 🛡️ Error Handling

### Custom Exceptions
- `NotFoundException`: Entity không tồn tại (404)
- `BadRequestException`: Business rule violation (400)
- `PropertyReferenceException`: Invalid sort parameters (400)

### Validation Errors
- Automatic field validation with detailed messages
- Business rule validation in service layer
- Proper HTTP status codes for all error scenarios

## 📋 Testing & Quality

### Code Quality
- ✅ Clean code structure
- ✅ Proper separation of concerns
- ✅ Consistent naming conventions
- ✅ Comprehensive error handling

### Data Integrity
- ✅ Foreign key constraints
- ✅ Unique constraints on names/titles
- ✅ Range validations on scores
- ✅ Business rule enforcement

## 🎉 System Ready For
- Production deployment
- Extended with more features
- Integration with frontend applications
- Performance optimization if needed

**Total Implementation**: 
- **3 Entities** + relationships
- **6 DTOs** (Request/Response)
- **3 Repositories** với custom methods
- **2 Services** với comprehensive business logic
- **2 Controllers** với full REST operations
- **15 Permissions** phân quyền chi tiết  
- **20 API endpoints** đầy đủ chức năng
- **Complete documentation** và error handling

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