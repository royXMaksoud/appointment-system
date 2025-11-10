# Admin APIs Complete Guide

This guide provides comprehensive documentation for all Admin APIs in the Appointment Service.

## Base URLs

- **Local Development**: `http://localhost:8084`
- **Via Gateway**: `http://localhost:8080/appointment-service`
- **Swagger UI**: `http://localhost:8084/swagger-ui/index.html`

## Authentication

All Admin APIs require JWT authentication. Include the token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

---

## 📋 Service Type Management APIs

Service Types represent the categories and sub-categories of services offered by centers (e.g., "Consultation", "Lab Tests", "X-Ray").

### Base Path
- `/api/admin/service-types`
- `/api/admin/ServiceTypes` (alternate)

### 1. Create Service Type

**POST** `/api/admin/service-types`

Creates a new service type with validation for unique name.

**Request Body:**
```json
{
  "name": "Consultation Services",
  "description": "General consultation and check-up services",
  "parentId": null,
  "isActive": true,
  "isLeaf": false,
  "code": "CONS",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** `201 Created`
```json
{
  "serviceTypeId": "650e8400-e29b-41d4-a716-446655440001",
  "name": "Consultation Services",
  "description": "General consultation and check-up services",
  "parentId": null,
  "isActive": true,
  "isDeleted": false,
  "isLeaf": false,
  "code": "CONS",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:00:00Z",
  "rowVersion": 0
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/service-types
```

---

### 2. Update Service Type

**PUT** `/api/admin/service-types/{serviceTypeId}`

Updates an existing service type.

**Path Parameter:**
- `serviceTypeId` (UUID): The unique identifier of the service type

**Request Body:**
```json
{
  "name": "Updated Consultation Services",
  "description": "Updated description",
  "parentId": null,
  "isActive": true,
  "isLeaf": false,
  "code": "CONS",
  "displayOrder": 1
}
```

**Response:** `200 OK`
```json
{
  "serviceTypeId": "650e8400-e29b-41d4-a716-446655440001",
  "name": "Updated Consultation Services",
  "description": "Updated description",
  "parentId": null,
  "isActive": true,
  "isDeleted": false,
  "isLeaf": false,
  "code": "CONS",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:30:00Z",
  "rowVersion": 1
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/650e8400-e29b-41d4-a716-446655440001
```

---

### 3. Get Service Type by ID

**GET** `/api/admin/service-types/{serviceTypeId}`

Retrieves a specific service type by its ID.

**Path Parameter:**
- `serviceTypeId` (UUID): The unique identifier

**Response:** `200 OK`
```json
{
  "serviceTypeId": "650e8400-e29b-41d4-a716-446655440001",
  "name": "Consultation Services",
  "description": "General consultation and check-up services",
  "parentId": null,
  "isActive": true,
  "isDeleted": false,
  "isLeaf": false,
  "code": "CONS",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:00:00Z",
  "rowVersion": 0
}
```

**Response:** `404 Not Found` - If service type doesn't exist

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/650e8400-e29b-41d4-a716-446655440001
```

---

### 4. Get All Service Types (Paginated)

**GET** `/api/admin/service-types?page=0&size=20&sort=displayOrder,asc`

Retrieves all service types with pagination support.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size
- `sort` (optional): Sorting criteria (e.g., `name,asc` or `displayOrder,desc`)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "serviceTypeId": "650e8400-e29b-41d4-a716-446655440001",
      "name": "Consultation Services",
      "description": "General consultation and check-up services",
      "parentId": null,
      "isActive": true,
      "isDeleted": false,
      "isLeaf": false,
      "code": "CONS",
      "displayOrder": 1,
      "createdById": "550e8400-e29b-41d4-a716-446655440000",
      "createdAt": "2025-10-30T12:00:00Z",
      "updatedAt": "2025-10-30T12:00:00Z",
      "rowVersion": 0
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/service-types?page=0&size=20&sort=displayOrder,asc
```

---

### 5. Delete Service Type (Soft Delete)

**DELETE** `/api/admin/service-types/{serviceTypeId}`

Soft deletes a service type (sets `isDeleted=true` and `isActive=false`).

**Path Parameter:**
- `serviceTypeId` (UUID): The unique identifier

**Response:** `204 No Content` - Success

**Response:** `404 Not Found` - If service type doesn't exist

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/650e8400-e29b-41d4-a716-446655440001
```

---

### 6. Advanced Filter Service Types

**POST** `/api/admin/service-types/filter?page=0&size=20`

Filters service types with advanced criteria and pagination.

**Request Body:**
```json
{
  "criteria": [
    {
      "field": "isActive",
      "operator": "EQUALS",
      "value": "true"
    },
    {
      "field": "name",
      "operator": "CONTAINS",
      "value": "Consultation"
    }
  ],
  "groups": [],
  "scopes": []
}
```

**Advanced Filter Example (AND condition):**
```json
{
  "criteria": [
    {
      "field": "isActive",
      "operator": "EQUALS",
      "value": "true"
    },
    {
      "field": "isLeaf",
      "operator": "EQUALS",
      "value": "true"
    }
  ]
}
```

**Advanced Filter Example (OR condition with groups):**
```json
{
  "groups": [
    {
      "logic": "OR",
      "criteria": [
        {
          "field": "code",
          "operator": "EQUALS",
          "value": "CONS"
        },
        {
          "field": "code",
          "operator": "EQUALS",
          "value": "LAB"
        }
      ]
    }
  ]
}
```

**Supported Operators:**
- `EQUALS`
- `NOT_EQUALS`
- `CONTAINS`
- `NOT_CONTAINS`
- `STARTS_WITH`
- `ENDS_WITH`
- `GREATER_THAN`
- `LESS_THAN`
- `GREATER_THAN_OR_EQUAL`
- `LESS_THAN_OR_EQUAL`
- `IN`
- `NOT_IN`
- `IS_NULL`
- `IS_NOT_NULL`

**Filterable Fields:**
- `serviceTypeId`
- `name`
- `description`
- `parentId`
- `isActive`
- `isDeleted`
- `isLeaf`
- `code`
- `displayOrder`
- `createdById`
- `createdAt`
- `updatedAt`

**Response:** `200 OK` (Same paginated structure as Get All)

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/filter
```

---

### 7. Get Filter Metadata

**GET** `/api/admin/service-types/meta`

Returns metadata for building dynamic filters (field definitions, sortable fields, pagination info).

**Response:** `200 OK`
```json
{
  "fields": {
    "serviceTypeId": {
      "type": "UUID",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "name": {
      "type": "String",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "code": {
      "type": "String",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "isActive": {
      "type": "Boolean",
      "nullable": false,
      "filterable": true,
      "sortable": true
    }
  },
  "sortableFields": ["name", "code", "displayOrder", "createdAt", "updatedAt"],
  "defaultPageSize": 20,
  "maxPageSize": 100
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/meta
```

---

### 8. Get Service Types Lookup (for Dropdowns)

**GET** `/api/admin/service-types/lookup`

Returns a simplified list of all active service types for use in dropdowns.

**Response:** `200 OK`
```json
[
  {
    "serviceTypeId": "650e8400-e29b-41d4-a716-446655440001",
    "name": "Consultation Services",
    "code": "CONS"
  },
  {
    "serviceTypeId": "650e8400-e29b-41d4-a716-446655440002",
    "name": "Lab Tests",
    "code": "LAB"
  }
]
```

**Direct Link:**
```
http://localhost:8084/api/admin/service-types/lookup
```

---

## 🎯 Action Type Management APIs

Action Types represent possible outcomes or actions for appointments (e.g., "Completed", "Cancelled", "Transferred", "No Show").

### Base Path
- `/api/admin/action-types`
- `/api/admin/ActionTypes` (alternate)

### 1. Create Action Type

**POST** `/api/admin/action-types`

Creates a new action type with validation for unique code.

**Request Body:**
```json
{
  "name": "Completed Successfully",
  "code": "COMPLETED",
  "description": "Appointment completed successfully",
  "isActive": true,
  "requiresTransfer": false,
  "completesAppointment": true,
  "color": "#28a745",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** `201 Created`
```json
{
  "actionTypeId": "750e8400-e29b-41d4-a716-446655440001",
  "name": "Completed Successfully",
  "code": "COMPLETED",
  "description": "Appointment completed successfully",
  "isActive": true,
  "isDeleted": false,
  "requiresTransfer": false,
  "completesAppointment": true,
  "color": "#28a745",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:00:00Z",
  "rowVersion": 0
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/action-types
```

---

### 2. Update Action Type

**PUT** `/api/admin/action-types/{actionTypeId}`

Updates an existing action type. Code field cannot be updated.

**Path Parameter:**
- `actionTypeId` (UUID): The unique identifier of the action type

**Request Body:**
```json
{
  "name": "Updated Completed Successfully",
  "code": "COMPLETED",
  "description": "Updated description",
  "isActive": true,
  "requiresTransfer": false,
  "completesAppointment": true,
  "color": "#28a745",
  "displayOrder": 1
}
```

**Response:** `200 OK`
```json
{
  "actionTypeId": "750e8400-e29b-41d4-a716-446655440001",
  "name": "Updated Completed Successfully",
  "code": "COMPLETED",
  "description": "Updated description",
  "isActive": true,
  "isDeleted": false,
  "requiresTransfer": false,
  "completesAppointment": true,
  "color": "#28a745",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:30:00Z",
  "rowVersion": 1
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/750e8400-e29b-41d4-a716-446655440001
```

---

### 3. Get Action Type by ID

**GET** `/api/admin/action-types/{actionTypeId}`

Retrieves a specific action type by its ID.

**Path Parameter:**
- `actionTypeId` (UUID): The unique identifier

**Response:** `200 OK`
```json
{
  "actionTypeId": "750e8400-e29b-41d4-a716-446655440001",
  "name": "Completed Successfully",
  "code": "COMPLETED",
  "description": "Appointment completed successfully",
  "isActive": true,
  "isDeleted": false,
  "requiresTransfer": false,
  "completesAppointment": true,
  "color": "#28a745",
  "displayOrder": 1,
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-30T12:00:00Z",
  "updatedAt": "2025-10-30T12:00:00Z",
  "rowVersion": 0
}
```

**Response:** `404 Not Found` - If action type doesn't exist

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/750e8400-e29b-41d4-a716-446655440001
```

---

### 4. Get All Action Types (Paginated)

**GET** `/api/admin/action-types?page=0&size=20&sort=displayOrder,asc`

Retrieves all action types with pagination support.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size
- `sort` (optional): Sorting criteria (e.g., `name,asc` or `displayOrder,desc`)

**Response:** `200 OK` (Same paginated structure as service types)

**Direct Link:**
```
http://localhost:8084/api/admin/action-types?page=0&size=20&sort=displayOrder,asc
```

---

### 5. Delete Action Type (Soft Delete)

**DELETE** `/api/admin/action-types/{actionTypeId}`

Soft deletes an action type (sets `isDeleted=true` and `isActive=false`).

**Path Parameter:**
- `actionTypeId` (UUID): The unique identifier

**Response:** `204 No Content` - Success

**Response:** `404 Not Found` - If action type doesn't exist

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/750e8400-e29b-41d4-a716-446655440001
```

---

### 6. Advanced Filter Action Types

**POST** `/api/admin/action-types/filter?page=0&size=20`

Filters action types with advanced criteria and pagination.

**Request Body:**
```json
{
  "criteria": [
    {
      "field": "isActive",
      "operator": "EQUALS",
      "value": "true"
    },
    {
      "field": "completesAppointment",
      "operator": "EQUALS",
      "value": "true"
    }
  ]
}
```

**Filter for Transfer Actions:**
```json
{
  "criteria": [
    {
      "field": "requiresTransfer",
      "operator": "EQUALS",
      "value": "true"
    }
  ]
}
```

**Filterable Fields:**
- `actionTypeId`
- `name`
- `code`
- `description`
- `isActive`
- `isDeleted`
- `requiresTransfer`
- `completesAppointment`
- `color`
- `displayOrder`
- `createdById`
- `createdAt`
- `updatedAt`

**Response:** `200 OK` (Same paginated structure)

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/filter
```

---

### 7. Get Filter Metadata

**GET** `/api/admin/action-types/meta`

Returns metadata for building dynamic filters.

**Response:** `200 OK`
```json
{
  "fields": {
    "actionTypeId": {
      "type": "UUID",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "name": {
      "type": "String",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "code": {
      "type": "String",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "requiresTransfer": {
      "type": "Boolean",
      "nullable": false,
      "filterable": true,
      "sortable": true
    },
    "completesAppointment": {
      "type": "Boolean",
      "nullable": false,
      "filterable": true,
      "sortable": true
    }
  },
  "sortableFields": ["name", "code", "displayOrder", "createdAt", "updatedAt"],
  "defaultPageSize": 20,
  "maxPageSize": 100
}
```

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/meta
```

---

### 8. Get Action Types Lookup (for Dropdowns)

**GET** `/api/admin/action-types/lookup`

Returns a simplified list of all active action types for use in dropdowns.

**Response:** `200 OK`
```json
[
  {
    "actionTypeId": "750e8400-e29b-41d4-a716-446655440001",
    "name": "Completed Successfully",
    "code": "COMPLETED",
    "requiresTransfer": false,
    "completesAppointment": true,
    "color": "#28a745"
  },
  {
    "actionTypeId": "750e8400-e29b-41d4-a716-446655440002",
    "name": "Transferred to Another Center",
    "code": "TRANSFERRED",
    "requiresTransfer": true,
    "completesAppointment": false,
    "color": "#ffc107"
  }
]
```

**Direct Link:**
```
http://localhost:8084/api/admin/action-types/lookup
```

---

## 🔐 Common Error Responses

All APIs follow standard HTTP status codes:

### 400 Bad Request
```json
{
  "timestamp": "2025-10-30T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2025-10-30T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-10-30T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 409 Conflict
```json
{
  "timestamp": "2025-10-30T12:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Service type name must be unique."
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-10-30T12:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 📊 Swagger/OpenAPI Documentation

Access interactive API documentation:

**Swagger UI**: `http://localhost:8084/swagger-ui/index.html`

**OpenAPI JSON**: `http://localhost:8084/v3/api-docs`

---

## 🧪 Testing with cURL

### Create Service Type
```bash
curl -X POST http://localhost:8084/api/admin/service-types \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Consultation Services",
    "description": "General consultation and check-up services",
    "parentId": null,
    "isActive": true,
    "isLeaf": false,
    "code": "CONS",
    "displayOrder": 1,
    "createdById": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### Get All Service Types
```bash
curl -X GET "http://localhost:8084/api/admin/service-types?page=0&size=20&sort=displayOrder,asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Filter Service Types
```bash
curl -X POST "http://localhost:8084/api/admin/service-types/filter?page=0&size=20" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "criteria": [
      {
        "field": "isActive",
        "operator": "EQUALS",
        "value": "true"
      }
    ]
  }'
```

---

## 📝 Notes

1. **Authentication**: All admin APIs require JWT authentication
2. **Pagination**: Default page size is 20, maximum is 100
3. **Soft Delete**: Delete operations are soft deletes (isDeleted flag)
4. **Optimistic Locking**: All updates use rowVersion for concurrency control
5. **Audit Trail**: All operations are logged with timestamps and user IDs
6. **Internationalization**: Error messages support both Arabic and English
7. **Validation**: All input is validated with appropriate error messages

---

## 🚀 Next Steps

After testing these APIs, you can:
1. Implement Schedule Management APIs
2. Implement Holiday Management APIs
3. Configure Gateway routing
4. Set up Eureka service registration
5. Create Postman collection for easier testing

---

## 📞 Support

For issues or questions:
- Check Swagger UI: `http://localhost:8084/swagger-ui/index.html`
- Review logs: `logs/appointment-service.log`
- Check database: `appointment_db` on PostgreSQL

---

**Last Updated**: October 30, 2025

