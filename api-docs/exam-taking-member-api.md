# Exam Taking (Member) API Documentation

> **APIs for members to view and take exams**

Base URL: `/api/exams`

---

## Authentication
All endpoints require authentication token in header:
```
Authorization: Bearer <token>
```

---

## Table of Contents
1. [Get Available Exams](#1-get-available-exams)
2. [Start Exam](#2-start-exam)
3. [Submit Exam](#3-submit-exam)
4. [Get My Exam Attempts](#4-get-my-exam-attempts)

---

## 1. Get Available Exams

Lấy danh sách các bài thi có thể làm (status = PUBLISHED).

### Endpoint
```http
GET /api/exams/available
```

### Required Permission
- `EXAM_VIEW_AVAILABLE`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |

### Request Example
```http
GET /api/exams/available?page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Available exams retrieved",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "JavaScript Fundamentals Test",
        "description": "Test your knowledge of JavaScript basics",
        "status": "PUBLISHED",
        "questionCount": 25
      },
      {
        "id": 2,
        "title": "React Advanced Concepts",
        "description": "Advanced React hooks and patterns",
        "status": "PUBLISHED",
        "questionCount": 30
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
    "totalPages": 5,
    "totalElements": 48,
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

### Response Error (401 Unauthorized)
```json
{
  "statusCode": 401,
  "message": "Unauthorized - Token invalid or expired",
  "data": null
}
```

### Response Error (403 Forbidden)
```json
{
  "statusCode": 403,
  "message": "Access denied - Missing required permission: EXAM_VIEW_AVAILABLE",
  "data": null
}
```

---

## 2. Start Exam

Bắt đầu làm bài thi. Hệ thống sẽ tạo một attempt mới và trả về đề thi với tất cả câu hỏi.

### Endpoint
```http
POST /api/exams/{examId}/start
```

### Required Permission
- `EXAM_TAKE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| examId | Long | Yes | ID của exam cần bắt đầu |

### Request Example
```http
POST /api/exams/1/start
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (201 Created)
```json
{
  "statusCode": 201,
  "message": "Attempt started",
  "data": {
    "attemptId": 156,
    "examId": 1,
    "title": "JavaScript Fundamentals Test",
    "totalQuestions": 25,
    "startedAt": "2025-10-23T10:30:00.000Z",
    "mustSubmitBefore": "2025-10-23T12:30:00.000Z",
    "questions": [
      {
        "id": 101,
        "text": "What is the output of: console.log(typeof null)?",
        "questionType": "MULTIPLE_CHOICE",
        "score": 4.0,
        "options": [
          {
            "id": 401,
            "content": "object"
          },
          {
            "id": 402,
            "content": "null"
          },
          {
            "id": 403,
            "content": "undefined"
          },
          {
            "id": 404,
            "content": "number"
          }
        ]
      },
      {
        "id": 102,
        "text": "Explain the concept of closures in JavaScript.",
        "questionType": "ESSAY",
        "score": 10.0,
        "options": []
      },
      {
        "id": 103,
        "text": "Is JavaScript a single-threaded language?",
        "questionType": "TRUE_FALSE",
        "score": 2.0,
        "options": [
          {
            "id": 501,
            "content": "True"
          },
          {
            "id": 502,
            "content": "False"
          }
        ]
      }
    ]
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Exam is not available or already started",
  "data": null
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam not found with id: 1",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "You already have an in-progress exam. Please finish it before starting a new one.",
  "data": null
}
```

### Notes
- **mustSubmitBefore**: Thời gian hết hạn nộp bài (nếu exam có giới hạn thời gian). Nếu không có giới hạn thì trả về `null`
- **questionType** có thể là:
  - `MULTIPLE_CHOICE`: Trắc nghiệm nhiều đáp án
  - `SINGLE_CHOICE`: Trắc nghiệm 1 đáp án
  - `TRUE_FALSE`: Đúng/Sai
  - `ESSAY`: Tự luận
- **options**: Mảng rỗng nếu là câu hỏi tự luận (ESSAY)

---

## 3. Submit Exam

Nộp bài thi và nhận kết quả chấm điểm tự động.

### Endpoint
```http
POST /api/exams/attempts/{attemptId}/submit
```

### Required Permission
- `EXAM_TAKE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| attemptId | Long | Yes | ID của attempt (nhận được từ API start exam) |

### Request Body
```json
{
  "answers": [
    {
      "questionId": 101,
      "selectedOptionIds": [401],
      "answerText": null
    },
    {
      "questionId": 102,
      "selectedOptionIds": [],
      "answerText": "Closures are functions that have access to variables from outer scope even after the outer function has returned. They allow functions to maintain access to their lexical scope."
    },
    {
      "questionId": 103,
      "selectedOptionIds": [501],
      "answerText": null
    }
  ]
}
```

### Request Body Schema
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| answers | Array\<AnswerPayload\> | Yes | Mảng các câu trả lời |

**AnswerPayload Schema:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| questionId | Long | Yes | ID của câu hỏi |
| selectedOptionIds | Array\<Long\> | No | Mảng ID các đáp án đã chọn (dùng cho MCQ/TF) |
| answerText | String | No | Câu trả lời dạng text (dùng cho ESSAY) |

### Request Example
```http
POST /api/exams/attempts/156/submit
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "answers": [
    {
      "questionId": 101,
      "selectedOptionIds": [401],
      "answerText": null
    },
    {
      "questionId": 102,
      "selectedOptionIds": [],
      "answerText": "Closures are functions that have access to variables from outer scope..."
    }
  ]
}
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "Attempt submitted & graded",
  "data": {
    "attemptId": 156,
    "score": 85.5,
    "totalCorrect": 20,
    "totalQuestions": 25
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Validation failed: answers cannot be null or empty",
  "data": null
}
```

### Response Error (404 Not Found)
```json
{
  "statusCode": 404,
  "message": "Exam attempt not found with id: 156",
  "data": null
}
```

### Response Error (409 Conflict)
```json
{
  "statusCode": 409,
  "message": "This exam has already been submitted",
  "data": null
}
```

### Notes
- **selectedOptionIds**: 
  - Với câu hỏi trắc nghiệm (MULTIPLE_CHOICE, SINGLE_CHOICE, TRUE_FALSE): Bắt buộc phải có ít nhất 1 optionId
  - Với câu tự luận (ESSAY): Để mảng rỗng []
- **answerText**: 
  - Với câu tự luận (ESSAY): Bắt buộc phải có text
  - Với câu trắc nghiệm: Để null hoặc bỏ qua field này
- **score**: Điểm tổng (đã tính toán tự động)
- **totalCorrect**: Số câu trả lời đúng (chỉ áp dụng cho câu trắc nghiệm)
- Câu hỏi tự luận (ESSAY) sẽ cần giáo viên chấm thủ công sau

---

## 4. Get My Exam Attempts

Xem lịch sử các lần làm bài thi của bản thân.

### Endpoint
```http
GET /api/exams/my/attempts
```

### Required Permission
- `EXAM_VIEW_HISTORY`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | No | 0 | Số trang (bắt đầu từ 0) |
| size | integer | No | 10 | Số lượng items mỗi trang |

### Request Example
```http
GET /api/exams/my/attempts?page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Success (200 OK)
```json
{
  "statusCode": 200,
  "message": "My attempts retrieved",
  "data": {
    "content": [
      {
        "attemptId": 156,
        "examId": 1,
        "examTitle": "JavaScript Fundamentals Test",
        "score": 85.5,
        "status": "GRADED",
        "submittedAt": "2025-10-23T11:45:00.000Z"
      },
      {
        "attemptId": 142,
        "examId": 3,
        "examTitle": "Python Basics",
        "score": 92.0,
        "status": "GRADED",
        "submittedAt": "2025-10-20T14:30:00.000Z"
      },
      {
        "attemptId": 148,
        "examId": 5,
        "examTitle": "Database Design",
        "score": null,
        "status": "IN_PROGRESS",
        "submittedAt": null
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

### Response Error (401 Unauthorized)
```json
{
  "statusCode": 401,
  "message": "Unauthorized - Token invalid or expired",
  "data": null
}
```

### Response Error (403 Forbidden)
```json
{
  "statusCode": 403,
  "message": "Access denied - Missing required permission: EXAM_VIEW_HISTORY",
  "data": null
}
```

### Notes
- **status** có 2 giá trị:
  - `IN_PROGRESS`: Đang làm bài (chưa nộp)
  - `GRADED`: Đã nộp và đã được chấm điểm
- **score**: Null nếu bài thi chưa được nộp hoặc chưa được chấm xong
- **submittedAt**: Null nếu bài thi chưa được nộp
- Kết quả được sắp xếp theo thời gian mới nhất (submittedAt DESC)

---

## Common Error Responses

### 400 Bad Request
Request không hợp lệ (thiếu field bắt buộc, format sai, validation error)

### 401 Unauthorized
Chưa đăng nhập hoặc token không hợp lệ/hết hạn

### 403 Forbidden
Không có quyền truy cập endpoint này (thiếu permission)

### 404 Not Found
Resource không tồn tại (exam, attempt, question không tìm thấy)

### 409 Conflict
Xung đột nghiệp vụ (đã có bài thi đang làm, bài thi đã submit, v.v.)

### 500 Internal Server Error
Lỗi server nội bộ

---

## Data Types Reference

### ExamCard
```typescript
{
  id: number;              // ID của exam
  title: string;           // Tiêu đề exam
  description: string;     // Mô tả exam
  status: string;          // "PUBLISHED"
  questionCount: number;   // Số lượng câu hỏi
}
```

### ExamStartResponse
```typescript
{
  attemptId: number;              // ID của attempt vừa tạo
  examId: number;                 // ID của exam
  title: string;                  // Tiêu đề exam
  totalQuestions: number;         // Tổng số câu hỏi
  startedAt: string;              // Thời gian bắt đầu (ISO 8601)
  mustSubmitBefore: string|null;  // Deadline nộp bài (ISO 8601) hoặc null
  questions: QuestionView[];      // Danh sách câu hỏi
}
```

### QuestionView
```typescript
{
  id: number;              // ID của question
  text: string;            // Nội dung câu hỏi
  questionType: string;    // "MULTIPLE_CHOICE" | "SINGLE_CHOICE" | "TRUE_FALSE" | "ESSAY"
  score: number;           // Điểm số của câu này
  options: OptionView[];   // Các đáp án (rỗng nếu là ESSAY)
}
```

### OptionView
```typescript
{
  id: number;      // ID của option
  content: string; // Nội dung đáp án
}
```

### ExamSubmitResponse
```typescript
{
  attemptId: number;      // ID của attempt
  score: number;          // Điểm tổng
  totalCorrect: number;   // Số câu đúng
  totalQuestions: number; // Tổng số câu
}
```

### AttemptSummary
```typescript
{
  attemptId: number;       // ID của attempt
  examId: number;          // ID của exam
  examTitle: string;       // Tiêu đề exam
  score: number|null;      // Điểm số (null nếu chưa nộp/chấm)
  status: string;          // "IN_PROGRESS" | "GRADED"
  submittedAt: string|null; // Thời gian nộp (ISO 8601) hoặc null
}
```

---

## Integration Guide for Frontend

### 1. Flow làm bài thi

```javascript
// Step 1: Lấy danh sách bài thi có thể làm
const exams = await fetch('/api/exams/available?page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Step 2: User chọn exam và bắt đầu
const examStart = await fetch('/api/exams/1/start', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` }
});
// Lưu attemptId để submit sau

// Step 3: User trả lời câu hỏi trong FE
// ...

// Step 4: Submit bài thi
const result = await fetch(`/api/exams/attempts/${attemptId}/submit`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    answers: [
      { questionId: 101, selectedOptionIds: [401], answerText: null },
      { questionId: 102, selectedOptionIds: [], answerText: "My essay answer..." }
    ]
  })
});
```

### 2. Xử lý câu hỏi theo loại

```javascript
// MULTIPLE_CHOICE / SINGLE_CHOICE / TRUE_FALSE
const answer = {
  questionId: question.id,
  selectedOptionIds: [selectedOption.id], // hoặc nhiều ID nếu multiple choice
  answerText: null
};

// ESSAY
const answer = {
  questionId: question.id,
  selectedOptionIds: [],
  answerText: userInput
};
```

### 3. Xem lịch sử

```javascript
const history = await fetch('/api/exams/my/attempts?page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

---

## Changelog

### Version 1.0.0 (2025-10-23)
- Initial release
- 4 endpoints: available, start, submit, my attempts
