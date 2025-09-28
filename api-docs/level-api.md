# Level Management API Documentation

## Overview
The Level Management API provides endpoints for managing exam levels in the system. Levels represent different difficulty categories that can be used to organize exam templates.

## Authentication
All endpoints require authentication using JWT Bearer tokens.

## Permissions
The following permissions are required for different operations:
- `LEVEL_VIEW`: View levels
- `LEVEL_CREATE`: Create new levels  
- `LEVEL_UPDATE`: Update existing levels
- `LEVEL_DELETE`: Delete levels

## Endpoints

### Create Level
**POST** `/api/levels`

Creates a new exam level.

**Required Permission:** `LEVEL_CREATE`

**Request Body:**
```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "difficulty": "string (optional, values: EASY, MEDIUM, HARD)",
  "status": "string (optional, values: ACTIVE, INACTIVE, default: ACTIVE)",
  "minScore": "integer (optional, min: 0)",
  "maxScore": "integer (optional, max: 100)"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Level created successfully",
  "data": {
    "id": 1,
    "name": "Beginner",
    "description": "Dành cho người mới bắt đầu học",
    "difficulty": "EASY",
    "status": "ACTIVE",
    "minScore": 0,
    "maxScore": 40,
    "createdBy": "admin",
    "updatedBy": "admin", 
    "createdAt": "2025-09-28T10:30:00",
    "updatedAt": "2025-09-28T10:30:00",
    "totalExamTemplates": 0
  }
}
```

### Get Level by ID
**GET** `/api/levels/{id}`

Retrieves a specific level by ID.

**Required Permission:** `LEVEL_VIEW`

**Path Parameters:**
- `id` (integer): Level ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Level retrieved successfully",
  "data": {
    "id": 1,
    "name": "Beginner",
    "description": "Dành cho người mới bắt đầu học",
    "difficulty": "EASY",
    "status": "ACTIVE",
    "minScore": 0,
    "maxScore": 40,
    "createdBy": "admin",
    "updatedBy": "admin",
    "createdAt": "2025-09-28T10:30:00",
    "updatedAt": "2025-09-28T10:30:00",
    "totalExamTemplates": 5
  }
}
```

### Get All Levels (Paginated)
**GET** `/api/levels`

Retrieves all levels with pagination.

**Required Permission:** `LEVEL_VIEW`

**Query Parameters:**
- `page` (integer, optional, default: 0): Page number
- `size` (integer, optional, default: 20): Page size
- `sort` (string, optional): Sort criteria (e.g., "name,asc")

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Levels retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Beginner",
        "description": "Dành cho người mới bắt đầu học",
        "difficulty": "EASY",
        "status": "ACTIVE",
        "minScore": 0,
        "maxScore": 40,
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2025-09-28T10:30:00",
        "updatedAt": "2025-09-28T10:30:00",
        "totalExamTemplates": 5
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
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