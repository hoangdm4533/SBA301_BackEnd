# API Documentation Index

## 📚 Tổng quan
Repository này chứa documentation đầy đủ cho hệ thống quản lý học tập và thi cử, bao gồm các tính năng mới được phát triển.

## 🆕 Tính năng mới: Level & Exam Template System

### Chức năng "Soạn Đề" đã hoàn thành
- ✅ **Quản lý Level**: 5 API endpoints với full CRUD
- ✅ **Quản lý Exam Template**: 15 API endpoints với workflow management  
- ✅ **Question Management**: Thêm/xóa/sắp xếp câu hỏi trong đề thi
- ✅ **Permission System**: 15 permissions mới được thêm
- ✅ **Data Initialization**: 5 levels, 15 exam templates với dữ liệu mẫu

**📖 Chi tiết xem tại:**
- [Level API Documentation](./level-api.md)
- [Exam Template API Documentation](./exam-template-api.md)
- [System Overview](./level-exam-template-system.md)

## 📑 Danh sách API Documentation

### Core System APIs
- [**Authentication API**](./authentication-api.md) - Đăng nhập, đăng ký, JWT management
- [**Permission API**](./permission-api.md) - Quản lý quyền hạn
- [**Role API**](./role-api.md) - Quản lý vai trò
- [**Security Management API**](./security-management-api.md) - Bảo mật hệ thống
- [**Session Management API**](./session-management-api.md) - Quản lý phiên đăng nhập

### Educational System APIs  
- [**🆕 Level API**](./level-api.md) - **Quản lý cấp độ khó bài thi**
- [**🆕 Exam Template API**](./exam-template-api.md) - **Quản lý template đề thi**
- [**Grade API**](./grade.md) - Quản lý điểm số và đánh giá
- [**Lesson Plan API**](./lessonPlan.md) - Quản lý kế hoạch bài học

### System Documentation
- [**🆕 Level & Exam Template System**](./level-exam-template-system.md) - **Tổng quan hệ thống soạn đề**
- [**Error Response**](./error-response.md) - Format lỗi chuẩn

## 🔄 Recent Updates (September 28, 2025)

### ✨ New Features Added
1. **Level Management System**
   - 5 predefined levels (Beginner → Master)
   - Score range management (0-100)
   - Full CRUD with validation
   - 4 new permissions

2. **Exam Template Management System**
   - Complete template lifecycle (DRAFT → PUBLISHED → ARCHIVED)  
   - Question management (add/remove/reorder)
   - Approval workflow
   - 7 new permissions
   - 15 comprehensive API endpoints

3. **Enhanced Exception Handling**
   - Custom NotFoundException for 404 errors
   - Custom BadRequestException for validation  
   - PropertyReferenceException handler for invalid sort parameters
   - Proper HTTP status codes for all scenarios

### 🛠️ Technical Improvements
- **Pagination Support**: All list APIs support page/size/sort parameters
- **Advanced Filtering**: Filter by level, status, and other criteria
- **Automatic Statistics**: Question count and total points calculation
- **Data Seeding**: 20 initial records for testing
- **Clean Architecture**: Service layer, repository pattern, DTO mapping

## 🎯 API Usage Examples

### Authentication
```http
POST /api/auth/login
Authorization: Bearer <token>
```

### Level Management  
```http
GET /api/levels?page=0&size=5&sort=name,asc
POST /api/levels
PUT /api/levels/{id}
DELETE /api/levels/{id}
```

### Exam Template Management
```http
GET /api/exam-templates?page=0&size=10&sort=createdAt,desc
POST /api/exam-templates
PUT /api/exam-templates/{id}
POST /api/exam-templates/{id}/publish
POST /api/exam-templates/{id}/questions
```

## 🔐 Permission Requirements

### Level Operations
- `LEVEL_VIEW`, `LEVEL_CREATE`, `LEVEL_UPDATE`, `LEVEL_DELETE`

### Exam Template Operations  
- `EXAM_TEMPLATE_VIEW`, `EXAM_TEMPLATE_CREATE`, `EXAM_TEMPLATE_UPDATE`
- `EXAM_TEMPLATE_DELETE`, `EXAM_TEMPLATE_MANAGE_QUESTIONS`
- `EXAM_TEMPLATE_PUBLISH`, `EXAM_TEMPLATE_APPROVE`

## 📊 System Statistics

### Total API Endpoints: **45+**
- Authentication: 8 endpoints
- Authorization: 10 endpoints  
- **🆕 Level Management: 5 endpoints**
- **🆕 Exam Template Management: 15 endpoints**
- Grade Management: 7 endpoints
- Other features: 15+ endpoints

### Permissions: **30+**
- **🆕 Level: 4 permissions**
- **🆕 Exam Template: 7 permissions** 
- Authentication: 5 permissions
- Other modules: 20+ permissions

## 🚀 Getting Started

1. **Authentication**: Start with `/api/auth/login`
2. **View Levels**: `GET /api/levels` (requires `LEVEL_VIEW`)
3. **Create Exam Template**: `POST /api/exam-templates` (requires `EXAM_TEMPLATE_CREATE`)
4. **Add Questions**: `POST /api/exam-templates/{id}/questions`
5. **Publish Template**: `POST /api/exam-templates/{id}/publish`

## ⚡ Performance Notes

- All list APIs support pagination
- Efficient database queries with JPA
- Automatic caching where applicable
- Optimized joins for related data

## 🐛 Error Handling

All APIs return consistent error format:
```json
{
  "statusCode": 400,
  "message": "Detailed error message in Vietnamese",
  "data": null
}
```

Common status codes:
- `200`: Success
- `400`: Bad Request / Validation Error  
- `401`: Unauthorized
- `403`: Forbidden / Insufficient Permissions
- `404`: Not Found
- `500`: Internal Server Error

## 📝 Notes

- All timestamps in ISO format: `2025-09-28T16:45:00`
- Vietnamese error messages for better UX
- Comprehensive validation on all inputs
- Business rule enforcement in service layer
- Clean separation between entities, DTOs, and responses

---

**📞 Support**: Contact development team for any questions regarding the APIs.
**🔄 Last Updated**: September 28, 2025 - Added Level & Exam Template System