# Level Management API Documentation

## Overview
The Level Management API provides endpoints for managing exam levels in the system. Each level defines a score range and difficulty category for organizing exam templates.

## Base URL
```
/api/levels
```

## Authentication
All endpoints require JWT authentication:
```
Authorization: Bearer <token>
```

## Permissions
Required permissions for different operations:
- `LEVEL_VIEW`: View levels
- `LEVEL_CREATE`: Create new levels  
- `LEVEL_UPDATE`: Update existing levels
- `LEVEL_DELETE`: Delete levels

## Endpoints

### 1. Get All Levels
**GET** `/api/levels`

Retrieve all levels with pagination support.

**Required Permission:** `LEVEL_VIEW`

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (format: `field,direction`)

**Valid Sort Fields:**
- `id`, `name`, `minScore`, `maxScore`, `createdAt`, `updatedAt`

**Example Request:**
```http
GET /api/levels?page=0&size=5&sort=minScore,asc
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Levels retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Beginner",
        "description": "Dành cho người mới bắt đầu học",
        "minScore": 0,
        "maxScore": 40,
        "examTemplateCount": 5,
        "createdAt": "2025-09-28T10:00:00",
        "updatedAt": "2025-09-28T10:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5
    },
    "totalElements": 5,
    "totalPages": 1
  }
}
    "updatedBy": "admin", 
    "createdAt": "2025-09-28T10:30:00",
    "updatedAt": "2025-09-28T10:30:00",
    "totalExamTemplates": 0
  }
}
```

### 2. Get Level by ID
**GET** `/api/levels/{id}`

Retrieve specific level information by ID.

**Required Permission:** `LEVEL_VIEW`

**Path Parameters:**
- `id` (required): Level ID

**Example Request:**
```http
GET /api/levels/1
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Level retrieved successfully",
  "data": {
    "id": 1,
    "name": "Beginner",
    "description": "Dành cho người mới bắt đầu học",
    "minScore": 0,
    "maxScore": 40,
    "examTemplateCount": 5,
    "createdAt": "2025-09-28T10:00:00",
    "updatedAt": "2025-09-28T10:00:00"
  }
}
```

### 3. Create Level
**POST** `/api/levels`

Create a new level.

**Required Permission:** `LEVEL_CREATE`

**Request Body:**
```json
{
  "name": "Advanced",
  "description": "Dành cho người học nâng cao",
  "minScore": 80,
  "maxScore": 100
}
```

**Validation Rules:**
- `name`: Required, 1-100 characters, must be unique
- `description`: Optional, max 500 characters
- `minScore`: Required, >= 0, must be < maxScore
- `maxScore`: Required, > minScore, <= 100

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Level created successfully",
  "data": {
    "id": 6,
    "name": "Advanced",
    "description": "Dành cho người học nâng cao",
    "minScore": 80,
    "maxScore": 100,
    "examTemplateCount": 0,
    "createdAt": "2025-09-28T16:30:00",
    "updatedAt": "2025-09-28T16:30:00"
  }
}
```

### 4. Update Level
**PUT** `/api/levels/{id}`

Update existing level information.

**Required Permission:** `LEVEL_UPDATE`

**Path Parameters:**
- `id` (required): Level ID to update

**Request Body:**
```json
{
  "name": "Advanced Plus",
  "description": "Dành cho người học nâng cao và chuyên sâu",
  "minScore": 85,
  "maxScore": 100
}
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Level updated successfully",
  "data": {
    "id": 6,
    "name": "Advanced Plus", 
    "description": "Dành cho người học nâng cao và chuyên sâu",
    "minScore": 85,
    "maxScore": 100,
    "examTemplateCount": 0,
    "createdAt": "2025-09-28T16:30:00",
    "updatedAt": "2025-09-28T16:35:00"
  }
}
```

### 5. Delete Level
**DELETE** `/api/levels/{id}`

Delete a level (only if no exam templates are using it).

**Required Permission:** `LEVEL_DELETE`

**Path Parameters:**
- `id` (required): Level ID to delete

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Level deleted successfully",
  "data": null
}
```

## Error Responses

### Common HTTP Status Codes
- **400 Bad Request**: Invalid request data or validation failed
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Level not found
- **500 Internal Server Error**: Server error

### Example Error Responses

**404 Not Found:**
```json
{
  "statusCode": 404,
  "message": "Không tìm thấy level với id 999",
  "data": null
}
```

**400 Bad Request (Validation):**
```json
{
  "statusCode": 400,
  "message": "Level với tên này đã tồn tại",
  "data": null
}
```

**400 Bad Request (Cannot Delete):**
```json
{
  "statusCode": 400,
  "message": "Không thể xóa level vì đã có exam template sử dụng",
  "data": null
}
```

**400 Bad Request (Invalid Sort):**
```json
{
  "statusCode": 400,
  "message": "Invalid sort parameter. Please use format: sort=fieldName or sort=fieldName,desc. Valid fields for Level: id, name, minScore, maxScore, createdAt, updatedAt",
  "data": null
}
```

## Data Models

### LevelResponse
```json
{
  "id": "Long - Unique identifier",
  "name": "String - Level name",
  "description": "String - Level description", 
  "minScore": "Integer - Minimum score (0-100)",
  "maxScore": "Integer - Maximum score (0-100)",
  "examTemplateCount": "Integer - Count of exam templates using this level",
  "createdAt": "LocalDateTime - Creation timestamp",
  "updatedAt": "LocalDateTime - Last update timestamp"
}
```

### LevelRequest
```json
{
  "name": "String - Required, 1-100 chars, unique",
  "description": "String - Optional, max 500 chars",
  "minScore": "Integer - Required, 0-100, < maxScore",
  "maxScore": "Integer - Required, 0-100, > minScore"
}
```

## Initial Data

The system comes with 5 predefined levels:

1. **Beginner** (0-40 points): Dành cho người mới bắt đầu học
2. **Elementary** (41-55 points): Trình độ cơ bản, đã nắm được kiến thức căn bản
3. **Intermediate** (56-70 points): Trình độ trung cấp, có thể áp dụng kiến thức vào thực tế
4. **Advanced** (71-85 points): Trình độ cao, nắm vững và vận dụng tốt kiến thức
5. **Master** (86-100 points): Trình độ chuyên gia, hiểu sâu và có thể hướng dẫn người khác
        "unsorted": false
      },
      "offset": 0,
      "pageSize": 20,
      "pageNumber": 0,
      "unpaged": false,
      "paged": true
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
}
```

### Get Active Levels
**GET** `/api/levels/active`

Retrieves all active levels ordered by minimum score.

**Required Permission:** `LEVEL_VIEW`

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Active levels retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Beginner",
      "difficulty": "EASY",
      "status": "ACTIVE",
      "minScore": 0,
      "maxScore": 40,
      "totalExamTemplates": 5
    }
  ]
}
```

### Get Levels by Difficulty
**GET** `/api/levels/difficulty/{difficulty}`

Retrieves levels filtered by difficulty.

**Required Permission:** `LEVEL_VIEW`

**Path Parameters:**
- `difficulty` (string): Difficulty level (EASY, MEDIUM, HARD)

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Levels by difficulty retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Beginner",
      "difficulty": "EASY",
      "status": "ACTIVE"
    }
  ]
}
```

### Search Levels
**GET** `/api/levels/search`

Search levels by keyword with pagination.

**Required Permission:** `LEVEL_VIEW`

**Query Parameters:**
- `keyword` (string, required): Search keyword
- `page` (integer, optional, default: 0): Page number
- `size` (integer, optional, default: 20): Page size

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Levels search completed successfully",
  "data": {
    "content": [...],
    "pageable": {...},
    "totalElements": 1
  }
}
```

### Update Level
**PUT** `/api/levels/{id}`

Updates an existing level.

**Required Permission:** `LEVEL_UPDATE`

**Path Parameters:**
- `id` (integer): Level ID

**Request Body:**
```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "difficulty": "string (optional)",
  "status": "string (optional)",
  "minScore": "integer (optional)",
  "maxScore": "integer (optional)"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Level updated successfully",
  "data": {
    "id": 1,
    "name": "Updated Beginner",
    "description": "Updated description",
    "updatedAt": "2025-09-28T11:30:00"
  }
}
```

### Delete Level
**DELETE** `/api/levels/{id}`

Deletes a level. Cannot delete if level has associated exam templates.

**Required Permission:** `LEVEL_DELETE`

**Path Parameters:**
- `id` (integer): Level ID

**Response:** `204 No Content`

### Activate Level
**PATCH** `/api/levels/{id}/activate`

Activates a level.

**Required Permission:** `LEVEL_UPDATE`

**Path Parameters:**
- `id` (integer): Level ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Level activated successfully"
}
```

### Deactivate Level
**PATCH** `/api/levels/{id}/deactivate`

Deactivates a level.

**Required Permission:** `LEVEL_UPDATE`

**Path Parameters:**
- `id` (integer): Level ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Level deactivated successfully"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Level với tên này đã tồn tại",
  "data": null
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Insufficient permissions",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Không tìm thấy level với id 1",
  "data": null
}
```

### 409 Conflict
```json
{
  "success": false,
  "message": "Không thể xóa level vì còn có 5 exam template đang được sử dụng",
  "data": null
}
```