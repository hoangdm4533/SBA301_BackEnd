# Exam Template Management API Documentation

## Overview
The Exam Template Management API provides endpoints for creating and managing exam templates, including adding/removing questions and managing the publishing workflow.

## Authentication
All endpoints require authentication using JWT Bearer tokens.

## Permissions
The following permissions are required for different operations:
- `EXAM_TEMPLATE_VIEW`: View exam templates
- `EXAM_TEMPLATE_CREATE`: Create new exam templates
- `EXAM_TEMPLATE_UPDATE`: Update existing exam templates
- `EXAM_TEMPLATE_DELETE`: Delete exam templates
- `EXAM_TEMPLATE_MANAGE_QUESTIONS`: Add/remove/reorder questions in exam templates
- `EXAM_TEMPLATE_PUBLISH`: Publish exam templates
- `EXAM_TEMPLATE_APPROVE`: Approve exam templates

## Endpoints

### Create Exam Template
**POST** `/api/exam-templates`

Creates a new exam template.

**Required Permission:** `EXAM_TEMPLATE_CREATE`

**Request Body:**
```json
{
  "title": "string (required)",
  "description": "string (optional)",
  "levelId": "integer (required)",
  "difficulty": "string (optional, values: EASY, MEDIUM, HARD)",
  "duration": "integer (optional, min: 1) - duration in minutes",
  "totalQuestions": "integer (optional, min: 1)",
  "totalPoints": "double (optional, min: 0)"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Exam template created successfully",
  "data": {
    "id": 1,
    "title": "Basic Math Test",
    "description": "A basic math test for beginners",
    "levelId": 1,
    "levelName": "Beginner",
    "difficulty": "EASY",
    "status": "DRAFT",
    "duration": 60,
    "totalQuestions": 0,
    "totalPoints": 0.0,
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