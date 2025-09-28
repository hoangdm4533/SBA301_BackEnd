# Exam Template Management API Documentation

## Overview
The Exam Template Management API provides endpoints for creating and managing exam templates with a complete workflow from draft to published state, including question management and approval processes.

## Base URL
```
/api/exam-templates
```

## Authentication
All endpoints require JWT authentication:
```
Authorization: Bearer <token>
```

## Permissions
Required permissions for different operations:
- `EXAM_TEMPLATE_VIEW`: View exam templates
- `EXAM_TEMPLATE_CREATE`: Create new exam templates
- `EXAM_TEMPLATE_UPDATE`: Update existing exam templates
- `EXAM_TEMPLATE_DELETE`: Delete exam templates
- `EXAM_TEMPLATE_MANAGE_QUESTIONS`: Add/remove/reorder questions
- `EXAM_TEMPLATE_PUBLISH`: Publish exam templates
- `EXAM_TEMPLATE_APPROVE`: Approve exam templates

## Exam Template Status Workflow

1. **DRAFT** → **PUBLISHED** → **ARCHIVED**
2. Templates can be approved at any stage
3. Only DRAFT templates can be modified
4. PUBLISHED templates cannot be deleted

## Endpoints

### 1. Get All Exam Templates
**GET** `/api/exam-templates`

Retrieve all exam templates with pagination.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (format: `field,direction`)

**Valid Sort Fields:**
- `id`, `title`, `status`, `createdAt`, `updatedAt`, `level`

**Example Request:**
```http
GET /api/exam-templates?page=0&size=5&sort=createdAt,desc
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Exam templates retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Basic Mathematics Quiz",
        "description": "Kiểm tra kiến thức toán học cơ bản",
        "status": "PUBLISHED",
        "levelId": 1,
        "levelName": "Beginner",
        "totalQuestions": 10,
        "totalPoints": 100.0,
        "duration": 30,
        "createdBy": {
          "id": 1,
          "username": "admin"
        },
        "approvedBy": {
          "id": 1,
```

### 2. Get Exam Template by ID
**GET** `/api/exam-templates/{id}`

Retrieve specific exam template by ID.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Exam template retrieved successfully",
  "data": {
    "id": 1,
    "title": "Basic Mathematics Quiz",
    "description": "Kiểm tra kiến thức toán học cơ bản",
    "status": "PUBLISHED",
    "levelId": 1,
    "levelName": "Beginner",
    "totalQuestions": 10,
    "totalPoints": 100.0,
    "duration": 30,
    "createdBy": {
      "id": 1,
      "username": "admin"
    },
    "createdAt": "2025-09-28T10:00:00",
    "updatedAt": "2025-09-28T10:30:00"
  }
}
```

### 3. Create Exam Template
**POST** `/api/exam-templates`

Create a new exam template.

**Required Permission:** `EXAM_TEMPLATE_CREATE`

**Request Body:**
```json
{
  "title": "Advanced Physics Quiz",
  "description": "Comprehensive physics test",
  "levelId": 4,
  "duration": 60
}
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Exam template created successfully",
  "data": {
    "id": 16,
    "title": "Advanced Physics Quiz", 
    "description": "Comprehensive physics test",
    "status": "DRAFT",
    "levelId": 4,
    "levelName": "Advanced",
    "totalQuestions": 0,
    "totalPoints": 0.0,
    "duration": 60,
    "createdBy": {
      "id": 2,
      "username": "teacher1"
    },
    "createdAt": "2025-09-28T16:45:00",
    "updatedAt": "2025-09-28T16:45:00"
  }
}
```

For more detailed API documentation including all endpoints (Update, Delete, Question Management, Workflow Management, Error Responses, and Data Models), please refer to the complete documentation file.
    "createdBy": "teacher1",
    "updatedBy": "teacher1",
    "approvedBy": null,
    "createdAt": "2025-09-28T10:30:00",
    "updatedAt": "2025-09-28T10:30:00",
    "approvedAt": null,
    "questions": []
  }
}
```

### Get Exam Template by ID
**GET** `/api/exam-templates/{id}`

Retrieves a specific exam template by ID.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Path Parameters:**
- `id` (integer): Exam template ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Exam template retrieved successfully",
  "data": {
    "id": 1,
    "title": "Basic Math Test",
    "description": "A basic math test for beginners",
    "levelId": 1,
    "levelName": "Beginner",
    "difficulty": "EASY",
    "status": "PUBLISHED",
    "duration": 60,
    "totalQuestions": 10,
    "totalPoints": 100.0,
    "createdBy": "teacher1",
    "updatedBy": "teacher1",
    "approvedBy": "admin",
    "createdAt": "2025-09-28T10:30:00",
    "updatedAt": "2025-09-28T11:00:00",
    "approvedAt": "2025-09-28T11:30:00",
    "questions": [
      {
        "id": 1,
        "examTemplateId": 1,
        "questionId": 101,
        "questionText": "What is 2 + 2?",
        "questionType": "MULTIPLE_CHOICE",
        "questionDifficulty": "EASY",
        "questionOrder": 1,
        "points": 10.0,
        "note": null,
        "createdAt": "2025-09-28T10:45:00"
      }
    ]
  }
}
```

### Get All Exam Templates (Paginated)
**GET** `/api/exam-templates`

Retrieves all exam templates with pagination.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Query Parameters:**
- `page` (integer, optional, default: 0): Page number
- `size` (integer, optional, default: 20): Page size
- `sort` (string, optional): Sort criteria

**Response:** `200 OK` - Paginated response similar to levels

### Get Exam Templates by Level
**GET** `/api/exam-templates/level/{levelId}`

Retrieves published exam templates for a specific level.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Path Parameters:**
- `levelId` (integer): Level ID

**Response:** `200 OK` - Paginated response

### Get Exam Templates by Status
**GET** `/api/exam-templates/status/{status}`

Retrieves exam templates filtered by status.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Path Parameters:**
- `status` (string): Status (DRAFT, PUBLISHED, ARCHIVED)

**Response:** `200 OK` - Paginated response

### Search Exam Templates
**GET** `/api/exam-templates/search`

Search exam templates by keyword.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Query Parameters:**
- `keyword` (string, required): Search keyword
- `page`, `size`, `sort`: Pagination parameters

**Response:** `200 OK` - Paginated response

### Search Exam Templates by Level
**GET** `/api/exam-templates/search/level/{levelId}`

Search exam templates by keyword within a specific level.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Path Parameters:**
- `levelId` (integer): Level ID

**Query Parameters:**
- `keyword` (string, required): Search keyword
- Pagination parameters

**Response:** `200 OK` - Paginated response

### Update Exam Template
**PUT** `/api/exam-templates/{id}`

Updates an existing exam template. Cannot update published or archived templates.

**Required Permission:** `EXAM_TEMPLATE_UPDATE`

**Path Parameters:**
- `id` (integer): Exam template ID

**Request Body:** Same as create request

**Response:** `200 OK` - Updated exam template data

### Delete Exam Template
**DELETE** `/api/exam-templates/{id}`

Deletes an exam template. Cannot delete published templates.

**Required Permission:** `EXAM_TEMPLATE_DELETE`

**Path Parameters:**
- `id` (integer): Exam template ID

**Response:** `204 No Content`

## Question Management

### Add Question to Exam Template
**POST** `/api/exam-templates/{examTemplateId}/questions`

Adds a question to an exam template.

**Required Permission:** `EXAM_TEMPLATE_MANAGE_QUESTIONS`

**Path Parameters:**
- `examTemplateId` (integer): Exam template ID

**Request Body:**
```json
{
  "questionId": "integer (required)",
  "questionOrder": "integer (required, min: 1)",
  "points": "double (optional, min: 0)",
  "note": "string (optional)"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Question added to exam template successfully",
  "data": {
    "id": 1,
    "examTemplateId": 1,
    "questionId": 101,
    "questionText": "What is 2 + 2?",
    "questionType": "MULTIPLE_CHOICE",
    "questionDifficulty": "EASY",
    "questionOrder": 1,
    "points": 10.0,
    "note": null,
    "createdAt": "2025-09-28T10:45:00"
  }
}
```

### Remove Question from Exam Template
**DELETE** `/api/exam-templates/{examTemplateId}/questions/{questionId}`

Removes a question from an exam template.

**Required Permission:** `EXAM_TEMPLATE_MANAGE_QUESTIONS`

**Path Parameters:**
- `examTemplateId` (integer): Exam template ID
- `questionId` (integer): Question ID

**Response:** `204 No Content`

### Update Question in Exam Template
**PUT** `/api/exam-templates/{examTemplateId}/questions/{questionId}`

Updates question details in an exam template.

**Required Permission:** `EXAM_TEMPLATE_MANAGE_QUESTIONS`

**Path Parameters:**
- `examTemplateId` (integer): Exam template ID
- `questionId` (integer): Question ID

**Request Body:** Same as add question request

**Response:** `200 OK`

### Get Questions in Exam Template
**GET** `/api/exam-templates/{examTemplateId}/questions`

Retrieves all questions in an exam template.

**Required Permission:** `EXAM_TEMPLATE_VIEW`

**Path Parameters:**
- `examTemplateId` (integer): Exam template ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Questions in exam template retrieved successfully",
  "data": [
    {
      "id": 1,
      "examTemplateId": 1,
      "questionId": 101,
      "questionText": "What is 2 + 2?",
      "questionType": "MULTIPLE_CHOICE",
      "questionDifficulty": "EASY",
      "questionOrder": 1,
      "points": 10.0,
      "note": null,
      "createdAt": "2025-09-28T10:45:00"
    }
  ]
}
```

### Reorder Questions
**PUT** `/api/exam-templates/{examTemplateId}/questions/reorder`

Reorders questions in an exam template.

**Required Permission:** `EXAM_TEMPLATE_MANAGE_QUESTIONS`

**Path Parameters:**
- `examTemplateId` (integer): Exam template ID

**Request Body:**
```json
[101, 102, 103, 104]
```
Array of question IDs in the desired order.

**Response:** `200 OK`

## Status Management

### Publish Exam Template
**PATCH** `/api/exam-templates/{id}/publish`

Publishes an exam template. Template must have at least one question.

**Required Permission:** `EXAM_TEMPLATE_PUBLISH`

**Path Parameters:**
- `id` (integer): Exam template ID

**Response:** `200 OK`

### Archive Exam Template
**PATCH** `/api/exam-templates/{id}/archive`

Archives an exam template.

**Required Permission:** `EXAM_TEMPLATE_UPDATE`

**Path Parameters:**
- `id` (integer): Exam template ID

**Response:** `200 OK`

### Approve Exam Template
**PATCH** `/api/exam-templates/{id}/approve`

Approves an exam template.

**Required Permission:** `EXAM_TEMPLATE_APPROVE`

**Path Parameters:**
- `id` (integer): Exam template ID

**Response:** `200 OK`

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Exam template với tiêu đề này đã tồn tại",
  "data": null
}
```

### 409 Conflict
```json
{
  "success": false,
  "message": "Không thể cập nhật exam template đã được publish hoặc archive",
  "data": null
}
```

### 422 Unprocessable Entity
```json
{
  "success": false,
  "message": "Không thể publish exam template không có câu hỏi nào",
  "data": null
}
```