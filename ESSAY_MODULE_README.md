# Essay Module - Complete Documentation

## Overview
The Essay Module allows **premium students** to complete timed essay assignments with **image upload support**, and **teachers** to create questions and manually grade submissions.

---

## Features

### ✅ For Students (Premium Only)
- View available essay questions
- Start essay (timer begins)
- Upload multiple images (max 10, 5MB each)
- Submit essay with answer text + images
- View submission history and grades
- Receive detailed teacher feedback

### ✅ For Teachers
- Create essay questions with rubric
- Set time limits (1-180 minutes)
- View pending submissions
- Grade submissions manually
- Provide detailed feedback
- View all submissions for a question

### ✅ System Features
- **Premium check**: Only users with active subscriptions can access
- **Time limit enforcement**: Submissions expire after time limit
- **Image support**: Students can upload images (diagrams, screenshots, etc.)
- **Activity logging**: All actions are logged
- **Security**: Role-based access control

---

## Database Setup

### Run Migration

```powershell
# Option 1: Using PowerShell script
.\run-essay-migration.ps1

# Option 2: Using MySQL directly
mysql -u root -p exam_management < db/migration/essay_module.sql
```

### Tables Created

#### `essay_questions`
- `id` - Primary key
- `prompt` - Question text
- `rubric` - Grading criteria
- `time_limit_minutes` - Time limit
- `max_score` - Maximum score
- `status` - ACTIVE, INACTIVE, ARCHIVED
- `created_by` - Teacher user ID
- `created_at`, `updated_at` - Timestamps

#### `essay_submissions`
- `id` - Primary key
- `user_id` - Student user ID
- `essay_question_id` - Question reference
- `answer` - Student answer text
- `image_urls` - JSON array of image URLs
- `started_at`, `submitted_at` - Timestamps
- `time_spent_seconds` - Time taken
- `status` - ONGOING, SUBMITTED, GRADED, EXPIRED
- `score`, `feedback`, `detailed_feedback` - Grading info
- `graded_by` - Teacher user ID
- `graded_at` - Grading timestamp

---

## API Endpoints

### Student Endpoints

#### 1. Get Active Questions
```http
GET /api/student/essays/questions
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "prompt": "Discuss the impact of AI...",
        "rubric": "Content (40%), Structure (30%)...",
        "time_limit_minutes": 30,
        "max_score": 100,
        "status": "ACTIVE",
        "created_by": "teacher@example.com",
        "created_at": "2025-10-29T10:00:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 2. Start Essay
```http
POST /api/student/essays/start
Authorization: Bearer {token}
Content-Type: application/json

{
  "essay_question_id": 1
}
```

**Response:**
```json
{
  "code": 201,
  "message": "Essay started successfully",
  "data": {
    "id": 1,
    "essay_question_id": 1,
    "prompt": "Discuss the impact of AI...",
    "answer": null,
    "image_urls": null,
    "started_at": "2025-10-29T10:00:00",
    "time_remaining_seconds": 1800,
    "time_limit_seconds": 1800,
    "status": "ONGOING"
  }
}
```

**Error (Non-Premium):**
```json
{
  "code": 403,
  "message": "Essay feature is only available for premium users",
  "data": null
}
```

#### 3. Upload Image
```http
POST /api/essay-images/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [image file]
```

**Response:**
```json
{
  "code": 201,
  "message": "Image uploaded successfully",
  "data": {
    "image_url": "http://localhost:9000/bucket/essay-images/123/1730198400_1234.jpg",
    "object_key": "essay-images/123/1730198400_1234.jpg",
    "size": 524288,
    "content_type": "image/jpeg"
  }
}
```

#### 4. Upload Multiple Images
```http
POST /api/essay-images/upload-multiple
Authorization: Bearer {token}
Content-Type: multipart/form-data

files: [image file 1], [image file 2], ...
```

**Response:**
```json
{
  "code": 201,
  "message": "Uploaded 3/3 images successfully",
  "data": {
    "uploaded": [
      {
        "image_url": "http://localhost:9000/bucket/essay-images/123/image1.jpg",
        "object_key": "essay-images/123/image1.jpg",
        "size": 524288,
        "index": 0
      }
    ],
    "uploaded_count": 3,
    "failed_count": 0,
    "errors": []
  }
}
```

#### 5. Submit Essay
```http
POST /api/student/essays/submit
Authorization: Bearer {token}
Content-Type: application/json

{
  "submission_id": 1,
  "answer": "Artificial Intelligence has revolutionized...",
  "image_urls": [
    "http://localhost:9000/bucket/essay-images/123/diagram1.jpg",
    "http://localhost:9000/bucket/essay-images/123/chart2.png"
  ]
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Essay submitted successfully. Waiting for teacher grading.",
  "data": {
    "id": 1,
    "essay_question_id": 1,
    "answer": "Artificial Intelligence has revolutionized...",
    "image_urls": ["..."],
    "submitted_at": "2025-10-29T10:25:00",
    "time_spent_seconds": 1500,
    "status": "SUBMITTED"
  }
}
```

**Error (Time Expired):**
```json
{
  "code": 400,
  "message": "Time limit exceeded. Your submission has been marked as expired.",
  "data": null
}
```

#### 6. Get My Submissions
```http
GET /api/student/essays/submissions
Authorization: Bearer {token}
```

#### 7. Get Submission Details
```http
GET /api/student/essays/submissions/{id}
Authorization: Bearer {token}
```

---

### Teacher Endpoints

#### 1. Create Essay Question
```http
POST /api/teacher/essays/questions
Authorization: Bearer {token}
Content-Type: application/json

{
  "prompt": "Discuss the impact of AI on modern education",
  "rubric": "Content (40%): Depth of analysis\nStructure (30%): Organization\nLanguage (20%): Grammar\nCritical Thinking (10%): Originality",
  "time_limit_minutes": 30,
  "max_score": 100
}
```

**Response:**
```json
{
  "code": 201,
  "message": "Essay question created successfully",
  "data": {
    "id": 1,
    "prompt": "Discuss the impact of AI on modern education",
    "rubric": "Content (40%): ...",
    "time_limit_minutes": 30,
    "max_score": 100,
    "status": "ACTIVE",
    "created_by": "teacher@example.com",
    "created_at": "2025-10-29T09:00:00"
  }
}
```

#### 2. Get Pending Submissions
```http
GET /api/teacher/essays/submissions/pending
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "essay_question_id": 1,
        "prompt": "Discuss the impact of AI...",
        "answer": "Artificial Intelligence...",
        "image_urls": ["url1", "url2"],
        "submitted_at": "2025-10-29T10:25:00",
        "time_spent_seconds": 1500,
        "status": "SUBMITTED"
      }
    ],
    "totalElements": 5
  }
}
```

#### 3. Get Submission Details (with images)
```http
GET /api/teacher/essays/submissions/{id}
Authorization: Bearer {token}
```

#### 4. Grade Submission
```http
POST /api/teacher/essays/grade
Authorization: Bearer {token}
Content-Type: application/json

{
  "submission_id": 1,
  "score": 85,
  "feedback": "Excellent analysis with good examples. Well-structured essay.",
  "detailed_feedback": "Content: 38/40 - Strong analysis...\nStructure: 28/30 - Clear organization...\nLanguage: 18/20 - Minor grammar issues...\nCritical Thinking: 10/10 - Original insights"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Essay graded successfully",
  "data": {
    "id": 1,
    "score": 85,
    "max_score": 100,
    "feedback": "Excellent analysis...",
    "detailed_feedback": "Content: 38/40...",
    "graded_by": "teacher@example.com",
    "graded_at": "2025-10-29T11:00:00",
    "status": "GRADED"
  }
}
```

---

## Configuration

### Application Properties

```properties
# MinIO Configuration (for image storage)
minio.url=http://localhost:9000
minio.accessKey=minioadmin
minio.secretKey=minioadmin
minio.bucket=exam-management
```

### Premium Subscription
The essay feature requires an active premium subscription. The system checks:
- User has a subscription
- Subscription status is "ACTIVE"
- Subscription end date is in the future

---

## Flow Diagrams

### Student Essay Flow
```
1. Student logs in (with premium account)
2. GET /api/student/essays/questions (view available essays)
3. POST /api/student/essays/start (start essay, timer begins)
4. POST /api/essay-images/upload (upload images - optional, multiple times)
5. POST /api/student/essays/submit (submit answer + image URLs)
6. System validates time limit
7. Essay marked as SUBMITTED, waiting for teacher
```

### Teacher Grading Flow
```
1. Teacher logs in
2. GET /api/teacher/essays/submissions/pending (view pending essays)
3. GET /api/teacher/essays/submissions/{id} (view student answer + images)
4. Review answer and images
5. POST /api/teacher/essays/grade (provide score + feedback)
6. Student receives notification (via activity log)
```

---

## Image Upload Specifications

### Supported Formats
- JPG/JPEG
- PNG
- GIF
- WEBP

### File Size Limits
- **Single upload**: 5MB per image
- **Multiple upload**: 10 images max, 5MB each

### Storage Location
Images are stored in MinIO at: `essay-images/{user_id}/{timestamp}_{random}.{ext}`

### Security
- Only authenticated users can upload
- Users can only delete their own images
- Object keys include user ID to prevent unauthorized access

---

## Testing Guide

### 1. Create Premium User
```sql
-- Ensure user has active subscription
INSERT INTO subscriptions (user_id, plan_id, status, start_date, end_date)
VALUES (1, 1, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY));
```

### 2. Test Student Flow
```bash
# Get questions
curl -X GET "http://localhost:8080/api/student/essays/questions" \
  -H "Authorization: Bearer {token}"

# Start essay
curl -X POST "http://localhost:8080/api/student/essays/start" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"essay_question_id": 1}'

# Upload image
curl -X POST "http://localhost:8080/api/essay-images/upload" \
  -H "Authorization: Bearer {token}" \
  -F "file=@image.jpg"

# Submit essay
curl -X POST "http://localhost:8080/api/student/essays/submit" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"submission_id": 1, "answer": "My essay...", "image_urls": ["url1"]}'
```

### 3. Test Teacher Flow
```bash
# Create question
curl -X POST "http://localhost:8080/api/teacher/essays/questions" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Question?", "rubric": "Criteria", "time_limit_minutes": 30, "max_score": 100}'

# Get pending
curl -X GET "http://localhost:8080/api/teacher/essays/submissions/pending" \
  -H "Authorization: Bearer {token}"

# Grade submission
curl -X POST "http://localhost:8080/api/teacher/essays/grade" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"submission_id": 1, "score": 85, "feedback": "Good work"}'
```

---

## Troubleshooting

### Error: "Essay feature is only available for premium users"
**Solution**: Check subscription status:
```sql
SELECT * FROM subscriptions WHERE user_id = ? AND status = 'ACTIVE' AND end_date > NOW();
```

### Error: "Time limit exceeded"
**Solution**: Time is strictly enforced. Students must submit before time_limit_minutes expires from started_at timestamp.

### Error: "File size exceeds 5MB limit"
**Solution**: Compress images before uploading. Use JPEG with lower quality settings.

### Error: "Failed to upload image"
**Solution**: Check MinIO is running:
```bash
docker ps | grep minio
```

---

## Activity Logging

All essay actions are logged with ActivityType:
- `ESSAY_STARTED` - Student started an essay
- `ESSAY_SUBMITTED` - Student submitted an essay
- `ESSAY_GRADED` - Teacher graded a submission

View logs:
```sql
SELECT * FROM user_activity_logs WHERE activity_type IN ('ESSAY_STARTED', 'ESSAY_SUBMITTED', 'ESSAY_GRADED');
```

---

## Summary

✅ **Complete essay system implemented!**

- ✅ Premium-only access with subscription check
- ✅ Time limit enforcement
- ✅ Multi-image upload support (up to 10 images, 5MB each)
- ✅ Teacher manual grading with detailed feedback
- ✅ Activity logging
- ✅ Security with role-based access
- ✅ Database migration script
- ✅ Comprehensive API documentation

**Ready to use!** 🎉
