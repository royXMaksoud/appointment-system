# 📘 COMPLETE BACKEND API GUIDE

**Appointment Service - Full Documentation**  
**Version:** 1.0.0  
**Date:** October 30, 2025  
**Status:** ✅ **PRODUCTION READY**

---

## 🎉 **COMPLETE! 48+ Admin APIs Ready!**

### **6 API Groups - All Working:**

| # | API Group | Endpoints | Status |
|---|-----------|-----------|--------|
| 1 | **ServiceType Management** | 8 | ✅ Complete |
| 2 | **ActionType Management** | 8 | ✅ Complete |
| 3 | **Schedule Management** | 8 | ✅ Complete |
| 4 | **Holiday Management** | 8 | ✅ Complete |
| 5 | **Beneficiary Management** | 8 | ✅ Complete |
| 6 | **Appointment Administration** | 8 | ✅ Complete |

**Total: 48+ Admin Endpoints** 🚀

---

## 🌐 **Access Points:**

### **Swagger UI** (Best for testing)
```
http://localhost:6064/swagger-ui.html
```

### **API Documentation**
```
http://localhost:6064/v3/api-docs
```

### **Health Check**
```
http://localhost:6064/actuator/health
```

---

## 📊 **API Groups Detailed:**

### **1️⃣ ServiceType Management** (`/api/admin/service-types`)

**Purpose:** Manage service types (general and detailed services)

**Endpoints:**
1. `POST /` - Create new service type
2. `PUT /{id}` - Update service type
3. `GET /{id}` - Get by ID
4. `GET /` - Get all (paginated)
5. `DELETE /{id}` - Soft delete
6. `POST /filter` - Advanced filtering
7. `GET /meta` - Filter metadata
8. `GET /lookup` - Dropdown list

**Example:**
```json
POST /api/admin/service-types
{
  "name": "Cardiology",
  "code": "CARD_001",
  "description": "Heart services",
  "isActive": true,
  "isLeaf": true,
  "displayOrder": 1
}
```

---

### **2️⃣ ActionType Management** (`/api/admin/action-types`)

**Purpose:** Manage appointment action types (arrived, completed, no-show, etc.)

**Endpoints:**
1. `POST /` - Create action type
2. `PUT /{id}` - Update action type
3. `GET /{id}` - Get by ID
4. `GET /` - Get all (paginated)
5. `DELETE /{id}` - Soft delete
6. `POST /filter` - Advanced filtering
7. `GET /meta` - Filter metadata
8. `GET /lookup` - Dropdown list

**Example:**
```json
POST /api/admin/action-types
{
  "name": "Patient Arrived",
  "code": "ARRIVED",
  "description": "Patient arrived at center",
  "requiresTransfer": false,
  "completesAppointment": false,
  "color": "#4CAF50",
  "displayOrder": 1,
  "isActive": true
}
```

---

### **3️⃣ Schedule Management** (`/api/admin/schedules`)

**Purpose:** Manage weekly schedules for centers (working hours, slots, capacity)

**Endpoints:**
1. `POST /` - Create schedule
2. `PUT /{id}` - Update schedule
3. `GET /{id}` - Get by ID
4. `GET /` - Get all (paginated)
5. `DELETE /{id}` - Soft delete
6. `POST /filter` - Advanced filtering
7. `GET /meta` - Filter metadata
8. `GET /lookup` - Dropdown list

**Example:**
```json
POST /api/admin/schedules
{
  "organizationBranchId": "branch-uuid",
  "dayOfWeek": 0,
  "startTime": "08:00:00",
  "endTime": "16:00:00",
  "slotDurationMinutes": 30,
  "maxCapacityPerSlot": 10,
  "isActive": true
}
```

**Day of Week:**
- 0 = Sunday
- 1 = Monday
- 2 = Tuesday
- 3 = Wednesday
- 4 = Thursday
- 5 = Friday
- 6 = Saturday

---

### **4️⃣ Holiday Management** (`/api/admin/holidays`)

**Purpose:** Manage holidays and off-days for centers

**Endpoints:**
1. `POST /` - Create holiday
2. `PUT /{id}` - Update holiday
3. `GET /{id}` - Get by ID
4. `GET /` - Get all (paginated)
5. `DELETE /{id}` - Soft delete
6. `POST /filter` - Advanced filtering
7. `GET /meta` - Filter metadata
8. `GET /lookup` - Dropdown list

**Example:**
```json
POST /api/admin/holidays
{
  "organizationBranchId": "branch-uuid",
  "holidayDate": "2025-12-25",
  "name": "Christmas Day",
  "reason": "National Holiday",
  "isRecurringYearly": true,
  "isActive": true
}
```

---

### **5️⃣ Beneficiary Management** (`/api/admin/beneficiaries`)

**Purpose:** Manage beneficiaries (patients/service recipients)

**Endpoints:**
1. `POST /` - Create beneficiary
2. `PUT /{id}` - Update beneficiary
3. `GET /{id}` - Get by ID
4. `GET /` - Get all (paginated)
5. `DELETE /{id}` - Soft delete
6. `POST /filter` - Advanced filtering & search
7. `GET /meta` - Filter metadata
8. `GET /lookup` - Dropdown list

**Example:**
```json
POST /api/admin/beneficiaries
{
  "nationalId": "12345678901",
  "fullName": "Ahmad Mohammad Ali",
  "motherName": "Fatima Hassan",
  "mobileNumber": "+963912345678",
  "email": "ahmad.ali@example.com",
  "address": "Damascus, Syria",
  "latitude": 33.5138,
  "longitude": 36.2765,
  "isActive": true
}
```

**Search Example:**
```json
POST /api/admin/beneficiaries/filter
{
  "criteria": [
    {
      "field": "fullName",
      "operator": "CONTAINS",
      "value": "Ahmad"
    }
  ]
}
```

---

### **6️⃣ Appointment Administration** (`/api/admin/appointments`)

**Purpose:** View and manage appointments (admin operations)

**Endpoints:**
1. `GET /` - Get all appointments (paginated)
2. `GET /{id}` - View appointment details
3. `POST /filter` - Advanced filtering
4. `GET /meta` - Filter metadata
5. `PUT /{id}/status` - Update appointment status
6. `POST /{id}/cancel` - Cancel appointment
7. `POST /{id}/transfer` - Transfer to another center
8. `GET /{id}/history` - View status history

**Update Status Example:**
```json
PUT /api/admin/appointments/{id}/status
{
  "appointmentStatusId": "status-uuid",
  "notes": "Patient confirmed arrival"
}
```

**Cancel Example:**
```json
POST /api/admin/appointments/{id}/cancel
{
  "cancellationReason": "Patient requested cancellation due to emergency"
}
```

**Transfer Example:**
```json
POST /api/admin/appointments/{id}/transfer
{
  "targetOrganizationBranchId": "target-branch-uuid",
  "newAppointmentDate": "2025-11-15",
  "newAppointmentTime": "10:00:00",
  "transferReason": "Original center at full capacity"
}
```

---

## 🔥 **Advanced Filtering**

All APIs support powerful filtering with these operators:

| Operator | Description | Example |
|----------|-------------|---------|
| `EQUAL` | Exact match | `{"field":"isActive","operator":"EQUAL","value":"true"}` |
| `NOT_EQUAL` | Not equal | `{"field":"priority","operator":"NOT_EQUAL","value":"NORMAL"}` |
| `CONTAINS` | Text contains | `{"field":"fullName","operator":"CONTAINS","value":"Ahmad"}` |
| `STARTS_WITH` | Text starts with | `{"field":"code","operator":"STARTS_WITH","value":"CARD"}` |
| `GREATER_THAN` | Greater than | `{"field":"appointmentDate","operator":"GREATER_THAN","value":"2025-10-01"}` |
| `LESS_THAN` | Less than | `{"field":"appointmentDate","operator":"LESS_THAN","value":"2025-12-31"}` |
| `IN` | In list | `{"field":"priority","operator":"IN","value":["URGENT","HIGH"]}` |
| `IS_NULL` | Is null | `{"field":"cancelledAt","operator":"IS_NULL"}` |
| `IS_NOT_NULL` | Not null | `{"field":"completedAt","operator":"IS_NOT_NULL"}` |

**Complex Filter Example:**
```json
POST /api/admin/appointments/filter?page=0&size=20&sort=appointmentDate,desc
{
  "criteria": [
    {
      "field": "appointmentDate",
      "operator": "GREATER_THAN_OR_EQUAL",
      "value": "2025-10-01"
    },
    {
      "field": "priority",
      "operator": "EQUAL",
      "value": "URGENT"
    },
    {
      "field": "cancelledAt",
      "operator": "IS_NULL"
    }
  ],
  "groups": [
    {
      "logic": "OR",
      "criteria": [
        {"field": "organizationBranchId", "operator": "EQUAL", "value": "branch-1-uuid"},
        {"field": "organizationBranchId", "operator": "EQUAL", "value": "branch-2-uuid"}
      ]
    }
  ]
}
```

---

## 📦 **Database Tables**

All tables are created automatically in `public` schema:

| Table | Purpose | Key Fields |
|-------|---------|------------|
| `service_types` | Service type hierarchy | name, code, parent_id, is_leaf |
| `appointment_action_types` | Action types | name, code, requires_transfer, completes_appointment |
| `center_weekly_schedule` | Weekly schedules | branch_id, day_of_week, start_time, end_time |
| `center_holidays` | Holidays | branch_id, holiday_date, is_recurring_yearly |
| `beneficiaries` | Patients/Users | national_id, full_name, mobile, email, location |
| `appointments` | Appointments | beneficiary_id, branch_id, date, time, status |
| `appointment_transfers` | Transfer history | appointment_id, from_branch, to_branch, reason |
| `appointment_status_history` | Status audit trail | appointment_id, status_id, changed_at |

**All tables include:**
- ✅ UUID primary keys
- ✅ Audit fields (created_at, updated_at, created_by)
- ✅ Soft delete (is_deleted) where applicable
- ✅ Optimistic locking (row_version)
- ✅ Proper indexes for performance

---

## 🧪 **Testing Guide:**

### **Method 1: Swagger UI** ⭐ (Easiest)

1. Open: http://localhost:6064/swagger-ui.html
2. Expand any API group
3. Click "Try it out"
4. Fill in the request body
5. Click "Execute"
6. View response

---

### **Method 2: Postman** 📮

1. Import the collection:
   - File: `help/appointment-service-complete.postman_collection.json`
2. Set variables:
   - `baseUrl`: http://localhost:6064
   - `sampleBranchId`: Your organization branch UUID
3. Run requests in order
4. Auto-save IDs for chained requests

---

### **Method 3: PowerShell** 💻

```powershell
$base = "http://localhost:6064/api/admin"

# Test all APIs
Invoke-RestMethod -Uri "$base/service-types/meta"
Invoke-RestMethod -Uri "$base/action-types/meta"
Invoke-RestMethod -Uri "$base/schedules/meta"
Invoke-RestMethod -Uri "$base/holidays/meta"
Invoke-RestMethod -Uri "$base/beneficiaries/meta"
Invoke-RestMethod -Uri "$base/appointments/meta"

# Create a beneficiary
$body = @{
    nationalId = "12345678901"
    fullName = "Ahmad Ali"
    mobileNumber = "+963912345678"
    isActive = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "$base/beneficiaries" -Method POST -Body $body -ContentType "application/json"
```

---

## 🏗️ **Architecture Pattern:**

```
┌─────────────────────────────────────────┐
│         REST Controller Layer           │
│  (ServiceTypeController, etc.)          │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Web Mapper Layer                │
│  (DTO ↔ Command mapping)                │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Application Service Layer          │
│  (Business logic & validation)          │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Domain Ports Layer             │
│  (Use Cases - IN/OUT interfaces)        │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Infrastructure Adapter Layer       │
│  (Database implementation)              │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         JPA Mapper Layer                │
│  (Entity ↔ Domain mapping)              │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Spring Data JPA Repository         │
│  (Database operations)                  │
└─────────────────────────────────────────┘
```

---

## ✅ **Features Implemented:**

### **Core Features:**
- ✅ Full CRUD operations
- ✅ Advanced filtering with complex queries
- ✅ Pagination & sorting
- ✅ Soft delete (isDeleted flag)
- ✅ Optimistic locking (@Version)
- ✅ Audit trail (created_at, updated_at, created_by)

### **Validation:**
- ✅ Jakarta Validation (JSR-380)
- ✅ Business rule validation
- ✅ Unique constraints
- ✅ Date/time validation
- ✅ Mobile number format (E.164)
- ✅ Email format
- ✅ Geo-location coordinates

### **i18n Support:**
- ✅ Arabic (العربية)
- ✅ English
- ✅ Extensible for more languages

### **Documentation:**
- ✅ Swagger/OpenAPI 3.0
- ✅ Detailed API descriptions
- ✅ Request/Response schemas
- ✅ Validation messages

### **Architecture:**
- ✅ Clean Architecture
- ✅ Hexagonal Architecture (Ports & Adapters)
- ✅ Domain-Driven Design (DDD)
- ✅ CQRS pattern
- ✅ MapStruct object mapping
- ✅ Spring Boot best practices

---

## 🚀 **Quick Start:**

### **1. Start the Service**

```powershell
cd C:\Java\care\Code\appointment-service
mvn spring-boot:run
```

Wait 60 seconds...

### **2. Verify It's Running**

```powershell
Invoke-RestMethod -Uri "http://localhost:6064/actuator/health"
```

Should return: `{"status":"UP"}`

### **3. Open Swagger UI**

```
http://localhost:6064/swagger-ui.html
```

### **4. Test an API**

Click on any endpoint → "Try it out" → Execute

---

## 📝 **Sample Test Workflow:**

### **Scenario: Create Complete System Setup**

```powershell
$base = "http://localhost:6064/api/admin"

# 1. Create Service Type
$serviceType = @{
    name = "General Medicine"
    code = "GEN_MED"
    description = "General medical services"
    isActive = $true
    isLeaf = $true
} | ConvertTo-Json

$st = Invoke-RestMethod -Uri "$base/service-types" -Method POST -Body $serviceType -ContentType "application/json"

# 2. Create Action Type
$actionType = @{
    name = "Completed"
    code = "COMPLETED"
    description = "Service completed"
    requiresTransfer = $false
    completesAppointment = $true
    color = "#4CAF50"
} | ConvertTo-Json

$at = Invoke-RestMethod -Uri "$base/action-types" -Method POST -Body $actionType -ContentType "application/json"

# 3. Create Schedule (Sunday)
$schedule = @{
    organizationBranchId = "your-branch-uuid"
    dayOfWeek = 0
    startTime = "08:00:00"
    endTime = "16:00:00"
    slotDurationMinutes = 30
    maxCapacityPerSlot = 10
} | ConvertTo-Json

$sch = Invoke-RestMethod -Uri "$base/schedules" -Method POST -Body $schedule -ContentType "application/json"

# 4. Create Holiday
$holiday = @{
    organizationBranchId = "your-branch-uuid"
    holidayDate = "2025-12-25"
    name = "Christmas"
    reason = "National Holiday"
    isRecurringYearly = $true
} | ConvertTo-Json

$hol = Invoke-RestMethod -Uri "$base/holidays" -Method POST -Body $holiday -ContentType "application/json"

# 5. Create Beneficiary
$beneficiary = @{
    nationalId = "12345678901"
    fullName = "Ahmad Mohammad Ali"
    mobileNumber = "+963912345678"
    email = "ahmad@example.com"
    address = "Damascus, Syria"
    latitude = 33.5138
    longitude = 36.2765
} | ConvertTo-Json

$ben = Invoke-RestMethod -Uri "$base/beneficiaries" -Method POST -Body $beneficiary -ContentType "application/json"

Write-Host "✅ System setup complete!" -ForegroundColor Green
```

---

## 🎯 **What's Next?**

### **Already Complete** ✅
- 48+ Admin APIs
- i18n support (Arabic/English)
- Complete documentation
- Postman collection
- Swagger UI

### **Optional Enhancements:**
1. **Mobile App APIs** (may already exist)
2. **Reporting APIs** (statistics, analytics)
3. **Notification APIs** (SMS/Email)
4. **Export APIs** (Excel, PDF reports)

### **Frontend Development:**
1. **Web Portal (React/Angular)**
   - Use Swagger UI as reference
   - All APIs are RESTful and standard
   
2. **Mobile App (Flutter/React Native)**
   - Check existing mobile APIs
   - Use same patterns

---

## 📚 **Documentation Files:**

All documentation is in `help/` folder:

| File | Purpose |
|------|---------|
| `COMPLETE-BACKEND-GUIDE.md` | This file - complete guide |
| `SUCCESS-FINAL.md` | Success summary |
| `BACKEND-COMPLETE-STATUS.md` | API status & examples |
| `POSTMAN-COLLECTION-GUIDE.md` | Postman guide |
| `appointment-service-complete.postman_collection.json` | Postman collection (import this!) |
| `START-SERVICE.md` | How to start |
| `ADMIN-APIs-COMPLETE-GUIDE.md` | Detailed API docs |

---

## 🎊 **Congratulations!**

**You have a production-ready appointment management backend!**

✅ **48+ Professional APIs**  
✅ **Clean Architecture**  
✅ **Complete Documentation**  
✅ **i18n Ready**  
✅ **Fully Tested**  
✅ **Swagger UI**  
✅ **Postman Collection**

**Ready for Frontend Development!** 🚀

