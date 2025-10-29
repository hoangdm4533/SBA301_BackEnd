# Exam Management (Admin) API Documentation

> **APIs for admin to manage exams and questions**

Base URL: `/api/exams`

---

## Authentication
All endpoints require authentication token in header:
```
Authorization: Bearer <token>
```

---

## Table of Contents
1. [Create Exam](#1-create-exam)
2. [Get Exam by ID](#2-get-exam-by-id)
3. [Get All Exams](#3-get-all-exams)
4. [Update Exam](#4-update-exam)
5. [Delete Exam](#5-delete-exam)
6. [Get Exams by Status](#6-get-exams-by-status)
7. [Search Exams](#7-search-exams)
8. [Add Question to Exam](#8-add-question-to-exam)
9. [Remove Question from Exam](#9-remove-question-from-exam)
10. [Get Questions in Exam](#10-get-questions-in-exam)
11. [Publish Exam](#11-publish-exam)
12. [Archive Exam](#12-archive-exam)
13. [Get Published Exams](#13-get-published-exams)

---

## 1. Create Exam

Tạo một exam mới với status mặc định là DRAFT.

### Endpoint
```http
POST /api/exams
```

### Required Permission
- `EXAM_CREATE`

### Request Body
```json
{
  "title": "JavaScript Advanced Test",
  "description": "Comprehensive test covering advanced JavaScript concepts",
  "status": "DRAFT"
}
```

### Request Body Schema
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Tiêu đề exam (không được để trống) |
| description | String | No | Mô tả chi tiết về exam |
| status | String | No | Trạng thái: "DRAFT", "PUBLISHED", "ARCHIVED" (mặc định: "DRAFT") |

### Request Example
```http
POST /api/exams
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "JavaScript Advanced Test",
  "description": "Comprehensive test covering advanced JavaScript concepts",
  "status": "DRAFT"
}
```

### Response Success (201 Created)
```json
{
  "statusCode": 201,
  "message": "Exam created successfully",
  "data": {
    "id": 15,
    "title": "JavaScript Advanced Test",
    "description": "Comprehensive test covering advanced JavaScript concepts",
    "status": "DRAFT",
    "createdAt": "2025-10-23T10:30:00.123Z",
    "updatedAt": "2025-10-23T10:30:00.123Z",
    "questions": []
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Validation failed: Tiêu đề không được để trống",
  "data": null
}
```

### Response Error (403 Forbidden)
```json
{
  "statusCode": 403,
  "message": "Access denied - Missing required permission: EXAM_CREATE",
  "data": null
}
```

---

## 2. Get Exam by ID

Lấy thông tin chi tiết của một exam theo ID.

### Endpoint
```http
GET /api/exams/{id}
```

### Required Permission
- `EXAM_UPDATE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | ID của exam cần lấy |

### Request Example
```http
GET /api/exams/15
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam retrieved successfully",
  "data": {
    "id": 15,
    "title": "JavaScript Advanced Test",
    "description": "Comprehensive test covering advanced JavaScript concepts",
    "status": "DRAFT",
    "createdAt": "2025-10-23T10:30:00.123Z",
    "updatedAt": "2025-10-23T10:30:00.123Z",
    "questions": [
      {
        "id": 201,
        "examId": 15,
        "questionId": 88,
        "questionText": "What is a closure in JavaScript?",
        "questionType": "MULTIPLE_CHOICE",
        "score": 5.0
      },
      {
        "id": 202,
        "examId": 15,
        "questionId": 92,
        "questionText": "Explain async/await",
        "questionType": "ESSAY",
        "score": 10.0
      }
    ]
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

---

## 3. Get All Exams

Lấy danh sách tất cả exams có phân trang và sắp xếp.

### Endpoint
```http
GET /api/exams
```

### Required Permission
- `EXAM_UPDATE`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |
| sortBy | string | No | id | Trường để sắp xếp (id, title, createdAt, updatedAt) |
| sortDir | string | No | asc | Hướng sắp xếp: "asc" hoặc "desc" |

### Request Example
```http
GET /api/exams?page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exams retrieved successfully",
  "data": {
    "content": [
      {
        "id": 15,
        "title": "JavaScript Advanced Test",
        "description": "Comprehensive test covering advanced JavaScript concepts",
        "status": "DRAFT",
        "createdAt": "2025-10-23T10:30:00.123Z",
        "updatedAt": "2025-10-23T10:30:00.123Z",
        "questions": []
      },
      {
        "id": 14,
        "title": "Python Basics Quiz",
        "description": "Test your Python fundamentals",
        "status": "PUBLISHED",
        "createdAt": "2025-10-22T14:20:00.456Z",
        "updatedAt": "2025-10-22T14:25:00.789Z",
        "questions": []
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 8,
    "totalElements": 75,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

---

## 4. Update Exam

Cập nhật thông tin của một exam.

### Endpoint
```http
PUT /api/exams/{id}
```

### Required Permission
- `EXAM_UPDATE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | ID của exam cần cập nhật |

### Request Body
```json
{
  "title": "JavaScript Advanced Test - Updated",
  "description": "Comprehensive test covering advanced JavaScript concepts including ES6+",
  "status": "DRAFT"
}
```

### Request Body Schema
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Tiêu đề exam (không được để trống) |
| description | String | No | Mô tả chi tiết về exam |
| status | String | No | Trạng thái: "DRAFT", "PUBLISHED", "ARCHIVED" |

### Request Example
```http
PUT /api/exams/15
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "JavaScript Advanced Test - Updated",
  "description": "Comprehensive test covering advanced JavaScript concepts including ES6+",
  "status": "DRAFT"
}
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam updated successfully",
  "data": {
    "id": 15,
    "title": "JavaScript Advanced Test - Updated",
    "description": "Comprehensive test covering advanced JavaScript concepts including ES6+",
    "status": "DRAFT",
    "createdAt": "2025-10-23T10:30:00.123Z",
    "updatedAt": "2025-10-23T11:45:00.789Z",
    "questions": []
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

---

## 5. Delete Exam

Xóa một exam khỏi hệ thống.

### Endpoint
```http
DELETE /api/exams/{id}
```

### Required Permission
- `EXAM_DELETE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | ID của exam cần xóa |

### Request Example
```http
DELETE /api/exams/15
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam deleted successfully",
  "data": {
    "id": 15
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "Cannot delete exam with active attempts",
  "data": null
}
```

### Notes
- Không thể xóa exam nếu đã có học viên làm bài
- Nên sử dụng Archive thay vì Delete để giữ lại dữ liệu lịch sử

---

## 6. Get Exams by Status

Lấy danh sách exams theo trạng thái cụ thể.

### Endpoint
```http
GET /api/exams?status={status}
```

### Required Permission
- `EXAM_UPDATE`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| status | string | Yes | - | Trạng thái: "DRAFT", "PUBLISHED", "ARCHIVED" |
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |
| sortBy | string | No | id | Trường để sắp xếp |
| sortDir | string | No | asc | Hướng sắp xếp: "asc" hoặc "desc" |

### Request Example
```http
GET /api/exams?status=PUBLISHED&page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exams by status retrieved",
  "data": {
    "content": [
      {
        "id": 14,
        "title": "Python Basics Quiz",
        "description": "Test your Python fundamentals",
        "status": "PUBLISHED",
        "createdAt": "2025-10-22T14:20:00.456Z",
        "updatedAt": "2025-10-22T14:25:00.789Z",
        "questions": []
      },
      {
        "id": 12,
        "title": "React Fundamentals",
        "description": "React hooks and components",
        "status": "PUBLISHED",
        "createdAt": "2025-10-21T09:15:00.123Z",
        "updatedAt": "2025-10-21T09:20:00.456Z",
        "questions": []
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 3,
    "totalElements": 28,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Invalid status value. Must be DRAFT, PUBLISHED, or ARCHIVED",
  "data": null
}
```

---

## 7. Search Exams

Tìm kiếm exams theo từ khóa (title hoặc description).

### Endpoint
```http
GET /api/exams/search
```

### Required Permission
- `EXAM_UPDATE`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| keyword | string | Yes | - | Từ khóa tìm kiếm (tìm trong title và description) |
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |

### Request Example
```http
GET /api/exams/search?keyword=JavaScript&page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exams search results",
  "data": {
    "content": [
      {
        "id": 15,
        "title": "JavaScript Advanced Test",
        "description": "Comprehensive test covering advanced JavaScript concepts",
        "status": "DRAFT",
        "createdAt": "2025-10-23T10:30:00.123Z",
        "updatedAt": "2025-10-23T10:30:00.123Z",
        "questions": []
      },
      {
        "id": 8,
        "title": "JavaScript Basics",
        "description": "Introduction to JavaScript programming",
        "status": "PUBLISHED",
        "createdAt": "2025-10-15T13:20:00.456Z",
        "updatedAt": "2025-10-15T13:25:00.789Z",
        "questions": []
      }
    ],
    "pageable": {
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
  }
}
```

### Notes
- Tìm kiếm không phân biệt chữ hoa/thường
- Tìm trong cả title và description
- Hỗ trợ tìm kiếm một phần từ (partial match)

---

## 8. Add Question to Exam

Thêm một câu hỏi vào exam.

### Endpoint
```http
POST /api/exams/{examId}/questions
```

### Required Permission
- `EXAM_QUESTION_ADD`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| examId | Long | Yes | ID của exam cần thêm câu hỏi |

### Request Body
```json
{
  "questionId": 88,
  "score": 5.0
}
```

### Request Body Schema
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| questionId | Long | Yes | ID của question cần thêm vào exam |
| score | Double | Yes | Điểm số của câu hỏi này (>= 0) |

### Request Example
```http
POST /api/exams/15/questions
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "questionId": 88,
  "score": 5.0
}
```

### Response Success (201 Created)
```json
{
  "statusCode": 201,
  "message": "Question added to exam",
  "data": {
    "id": 203,
    "examId": 15,
    "questionId": 88,
    "questionText": "What is a closure in JavaScript?",
    "questionType": "MULTIPLE_CHOICE",
    "score": 5.0
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Question not found with id: 88",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "Question already exists in this exam",
  "data": null
}
```

### Notes
- Một question có thể được thêm vào nhiều exam khác nhau
- Mỗi question chỉ được thêm vào exam một lần
- Score có thể khác nhau giữa các exam

---

## 9. Remove Question from Exam

Xóa một câu hỏi khỏi exam.

### Endpoint
```http
DELETE /api/exams/{examId}/questions/{questionId}
```

### Required Permission
- `EXAM_QUESTION_REMOVE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| examId | Long | Yes | ID của exam |
| questionId | Long | Yes | ID của question cần xóa |

### Request Example
```http
DELETE /api/exams/15/questions/88
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Question removed from exam",
  "data": {
    "examId": 15,
    "questionId": 88
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Question not found in this exam",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "Cannot remove question from published exam with active attempts",
  "data": null
}
```

### Notes
- Không xóa question khỏi database, chỉ xóa liên kết với exam
- Cẩn trọng khi xóa question khỏi exam đã được publish và có học viên làm

---

## 10. Get Questions in Exam

Lấy danh sách tất cả câu hỏi trong một exam.

### Endpoint
```http
GET /api/exams/{examId}/questions
```

### Required Permission
- `EXAM_UPDATE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| examId | Long | Yes | ID của exam |

### Request Example
```http
GET /api/exams/15/questions
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam questions retrieved",
  "data": [
    {
      "id": 201,
      "examId": 15,
      "questionId": 88,
      "questionText": "What is a closure in JavaScript?",
      "questionType": "MULTIPLE_CHOICE",
      "score": 5.0
    },
    {
      "id": 202,
      "examId": 15,
      "questionId": 92,
      "questionText": "Explain async/await",
      "questionType": "ESSAY",
      "score": 10.0
    },
    {
      "id": 203,
      "examId": 15,
      "questionId": 95,
      "questionText": "JavaScript is a compiled language",
      "questionType": "TRUE_FALSE",
      "score": 2.0
    }
  ]
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

### Notes
- Trả về danh sách rỗng nếu exam chưa có câu hỏi nào
- Câu hỏi được sắp xếp theo thứ tự thêm vào

---

## 11. Publish Exam

Đổi trạng thái exam sang PUBLISHED để học viên có thể làm bài.

### Endpoint
```http
PUT /api/exams/{id}/publish
```

### Required Permission
- `EXAM_PUBLISH`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | ID của exam cần publish |

### Request Example
```http
PUT /api/exams/15/publish
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam published successfully",
  "data": {
    "id": 15
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Cannot publish exam without questions",
  "data": null
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "Exam is already published",
  "data": null
}
```

### Notes
- Chỉ có thể publish exam khi đã có ít nhất 1 câu hỏi
- Exam đã publish vẫn có thể chỉnh sửa nhưng cần cẩn trọng
- Học viên chỉ thấy exam có status = PUBLISHED

---

## 12. Archive Exam

Đổi trạng thái exam sang ARCHIVED để ẩn khỏi danh sách available.

### Endpoint
```http
PUT /api/exams/{id}/archive
```

### Required Permission
- `EXAM_ARCHIVE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | ID của exam cần archive |

### Request Example
```http
PUT /api/exams/15/archive
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Exam archived successfully",
  "data": {
    "id": 15
  }
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 15",
  "data": null
}
```

### Notes
- Exam archived vẫn giữ nguyên dữ liệu và lịch sử làm bài
- Học viên không thể bắt đầu exam mới khi exam đã archived
- Có thể unarchive bằng cách update status về DRAFT hoặc PUBLISHED

---

## 13. Get Published Exams

Lấy danh sách tất cả exams đã được publish.

### Endpoint
```http
GET /api/exams/published
```

### Required Permission
- `EXAM_UPDATE`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |

### Request Example
```http
GET /api/exams/published?page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Published exams retrieved",
  "data": {
    "content": [
      {
        "id": 14,
        "title": "Python Basics Quiz",
        "description": "Test your Python fundamentals",
        "status": "PUBLISHED",
        "createdAt": "2025-10-22T14:20:00.456Z",
        "updatedAt": "2025-10-22T14:25:00.789Z",
        "questions": []
      },
      {
        "id": 12,
        "title": "React Fundamentals",
        "description": "React hooks and components",
        "status": "PUBLISHED",
        "createdAt": "2025-10-21T09:15:00.123Z",
        "updatedAt": "2025-10-21T09:20:00.456Z",
        "questions": []
      }
    ],
    "pageable": {
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 3,
    "totalElements": 28,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### Notes
- Tương tự như Get Exams by Status với status=PUBLISHED
- Tiện lợi hơn khi cần lấy nhanh danh sách exams đã publish

---

## Common Error Responses

### 400 Bad Request
Request không hợp lệ (thiếu field bắt buộc, format sai, validation error)

### 401 Unauthorized
Chưa đăng nhập hoặc token không hợp lệ/hết hạn

### 403 Forbidden
Không có quyền truy cập endpoint này (thiếu permission)

### 404 Not Found
Resource không tồn tại (exam, question không tìm thấy)

### 409 Conflict
Xung đột nghiệp vụ (duplicate, exam đã publish, đã có attempts, v.v.)

### 500 Internal Server Error
Lỗi server nội bộ

---

## Data Types Reference

### ExamResponse
```typescript
{
  id: number;                        // ID của exam
  title: string;                     // Tiêu đề exam
  description: string;               // Mô tả exam
  status: string;                    // "DRAFT" | "PUBLISHED" | "ARCHIVED"
  createdAt: string;                 // Thời gian tạo (ISO 8601)
  updatedAt: string;                 // Thời gian cập nhật (ISO 8601)
  questions: ExamQuestionResponse[]; // Danh sách câu hỏi
}
```

### ExamQuestionResponse
```typescript
{
  id: number;          // ID của exam_question (quan hệ)
  examId: number;      // ID của exam
  questionId: number;  // ID của question
  questionText: string; // Nội dung câu hỏi
  questionType: string; // "MULTIPLE_CHOICE" | "SINGLE_CHOICE" | "TRUE_FALSE" | "ESSAY"
  score: number;       // Điểm số
}
```

---

## Integration Guide for Frontend

### 1. Tạo và quản lý exam

```javascript
// Step 1: Tạo exam mới
const createExam = await fetch('/api/exams', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: 'JavaScript Advanced Test',
    description: 'Comprehensive test',
    status: 'DRAFT'
  })
});
const exam = await createExam.json();
const examId = exam.data.id;

// Step 2: Thêm câu hỏi vào exam
await fetch(`/api/exams/${examId}/questions`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    questionId: 88,
    score: 5.0
  })
});

// Step 3: Publish exam
await fetch(`/api/exams/${examId}/publish`, {
  method: 'PUT',
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### 2. Tìm kiếm và lọc exams

```javascript
// Lấy tất cả exams
const allExams = await fetch('/api/exams?page=0&size=20&sortBy=createdAt&sortDir=desc', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Lọc theo status
const publishedExams = await fetch('/api/exams?status=PUBLISHED&page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Tìm kiếm
const searchResults = await fetch('/api/exams/search?keyword=JavaScript&page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Hoặc dùng endpoint riêng cho published
const published = await fetch('/api/exams/published?page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### 3. Quản lý câu hỏi trong exam

```javascript
// Lấy danh sách câu hỏi
const questions = await fetch(`/api/exams/${examId}/questions`, {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Xóa câu hỏi khỏi exam
await fetch(`/api/exams/${examId}/questions/${questionId}`, {
  method: 'DELETE',
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### 4. Cập nhật và xóa exam

```javascript
// Cập nhật exam
await fetch(`/api/exams/${examId}`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: 'Updated Title',
    description: 'Updated Description',
    status: 'DRAFT'
  })
});

// Archive exam
await fetch(`/api/exams/${examId}/archive`, {
  method: 'PUT',
  headers: { 'Authorization': `Bearer ${token}` }
});

// Xóa exam (cẩn trọng!)
await fetch(`/api/exams/${examId}`, {
  method: 'DELETE',
  headers: { 'Authorization': `Bearer ${token}` }
});
```

---

## Business Rules

### Exam Status Flow
```
DRAFT → PUBLISHED → ARCHIVED
  ↑         ↓
  └─────────┘
```

- **DRAFT**: Exam đang được tạo/chỉnh sửa, chưa public
- **PUBLISHED**: Exam đã sẵn sàng cho học viên làm bài
- **ARCHIVED**: Exam đã ngừng sử dụng nhưng giữ lại dữ liệu

### Validation Rules
1. Exam phải có ít nhất 1 câu hỏi mới được publish
2. Không thể xóa exam đã có học viên làm bài
3. Một question có thể thuộc nhiều exam
4. Mỗi question chỉ xuất hiện 1 lần trong 1 exam
5. Score của question phải >= 0
6. Title là bắt buộc khi tạo/cập nhật exam

### Best Practices
1. Luôn dùng DRAFT khi tạo exam mới
2. Kiểm tra kỹ trước khi publish
3. Dùng Archive thay vì Delete để giữ lịch sử
4. Cẩn trọng khi sửa exam đã published có attempts
5. Set score hợp lý cho từng câu hỏi dựa trên độ khó

---

## Changelog

### Version 1.0.0 (2025-10-23)
- Initial release
- 13 endpoints for full exam management
- CRUD operations + status management + question management
