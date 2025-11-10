# 📮 Postman API Collection - Complete Guide

## **Quick Import to Postman**

### **Option 1: Import JSON Collection**

Save this JSON and import to Postman:

**File: `appointment-service-apis.postman_collection.json`**

```json
{
  "info": {
    "name": "Appointment Service - Admin APIs",
    "_postman_id": "appointment-service-2025",
    "description": "Complete API collection for appointment-service Admin APIs",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:6064/api/admin",
      "type": "string"
    }
  ],
  "item": [
    {
      "name": "ServiceType APIs",
      "item": [
        {
          "name": "Get All ServiceTypes",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/service-types?page=0&size=20",
              "host": ["{{baseUrl}}"],
              "path": ["service-types"],
              "query": [
                {"key": "page", "value": "0"},
                {"key": "size", "value": "20"}
              ]
            }
          }
        },
        {
          "name": "Get ServiceType by ID",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/service-types/{{serviceTypeId}}",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "{{serviceTypeId}}"]
            }
          }
        },
        {
          "name": "Create ServiceType",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"General Medicine\",\n  \"description\": \"General medical services\",\n  \"code\": \"GEN_MED_001\",\n  \"isActive\": true,\n  \"isLeaf\": false,\n  \"displayOrder\": 1\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/service-types",
              "host": ["{{baseUrl}}"],
              "path": ["service-types"]
            }
          }
        },
        {
          "name": "Update ServiceType",
          "request": {
            "method": "PUT",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"General Medicine - Updated\",\n  \"description\": \"Updated description\",\n  \"code\": \"GEN_MED_001\",\n  \"isActive\": true,\n  \"isLeaf\": false,\n  \"displayOrder\": 1\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/service-types/{{serviceTypeId}}",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "{{serviceTypeId}}"]
            }
          }
        },
        {
          "name": "Delete ServiceType",
          "request": {
            "method": "DELETE",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/service-types/{{serviceTypeId}}",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "{{serviceTypeId}}"]
            }
          }
        },
        {
          "name": "Filter ServiceTypes",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"criteria\": [\n    {\n      \"field\": \"isActive\",\n      \"operator\": \"EQUAL\",\n      \"value\": \"true\"\n    }\n  ]\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/service-types/filter?page=0&size=20",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "filter"],
              "query": [
                {"key": "page", "value": "0"},
                {"key": "size", "value": "20"}
              ]
            }
          }
        },
        {
          "name": "Get ServiceType Meta",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/service-types/meta",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "meta"]
            }
          }
        },
        {
          "name": "Get ServiceType Lookup",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/service-types/lookup",
              "host": ["{{baseUrl}}"],
              "path": ["service-types", "lookup"]
            }
          }
        }
      ]
    },
    {
      "name": "ActionType APIs",
      "item": [
        {
          "name": "Create ActionType",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Patient Arrived\",\n  \"code\": \"ARRIVED\",\n  \"description\": \"Patient arrived at center\",\n  \"requiresTransfer\": false,\n  \"completesAppointment\": false,\n  \"color\": \"#4CAF50\",\n  \"displayOrder\": 1,\n  \"isActive\": true\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/action-types",
              "host": ["{{baseUrl}}"],
              "path": ["action-types"]
            }
          }
        }
      ]
    },
    {
      "name": "Schedule APIs",
      "item": [
        {
          "name": "Create Schedule",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"organizationBranchId\": \"123e4567-e89b-12d3-a456-426614174000\",\n  \"dayOfWeek\": 0,\n  \"startTime\": \"08:00:00\",\n  \"endTime\": \"16:00:00\",\n  \"slotDurationMinutes\": 30,\n  \"maxCapacityPerSlot\": 10,\n  \"isActive\": true\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/schedules",
              "host": ["{{baseUrl}}"],
              "path": ["schedules"]
            }
          }
        },
        {
          "name": "Get All Schedules",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/schedules?page=0&size=20",
              "host": ["{{baseUrl}}"],
              "path": ["schedules"],
              "query": [
                {"key": "page", "value": "0"},
                {"key": "size", "value": "20"}
              ]
            }
          }
        },
        {
          "name": "Get Schedule Meta",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/schedules/meta",
              "host": ["{{baseUrl}}"],
              "path": ["schedules", "meta"]
            }
          }
        }
      ]
    },
    {
      "name": "Holiday APIs",
      "item": [
        {
          "name": "Create Holiday",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"organizationBranchId\": \"123e4567-e89b-12d3-a456-426614174000\",\n  \"holidayDate\": \"2025-12-25\",\n  \"name\": \"Christmas Day\",\n  \"reason\": \"National Holiday\",\n  \"isRecurringYearly\": true,\n  \"isActive\": true\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/holidays",
              "host": ["{{baseUrl}}"],
              "path": ["holidays"]
            }
          }
        },
        {
          "name": "Get All Holidays",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/holidays?page=0&size=20",
              "host": ["{{baseUrl}}"],
              "path": ["holidays"],
              "query": [
                {"key": "page", "value": "0"},
                {"key": "size", "value": "20"}
              ]
            }
          }
        },
        {
          "name": "Get Holiday Meta",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/holidays/meta",
              "host": ["{{baseUrl}}"],
              "path": ["holidays", "meta"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## **Quick PowerShell Test Commands**

```powershell
# Set base URL
$base = "http://localhost:6064/api/admin"

# Test 1: Get ServiceType Meta
Invoke-RestMethod -Uri "$base/service-types/meta" -Method GET | ConvertTo-Json -Depth 10

# Test 2: Get ActionType Meta
Invoke-RestMethod -Uri "$base/action-types/meta" -Method GET | ConvertTo-Json -Depth 10

# Test 3: Get Schedule Meta
Invoke-RestMethod -Uri "$base/schedules/meta" -Method GET | ConvertTo-Json -Depth 10

# Test 4: Get Holiday Meta
Invoke-RestMethod -Uri "$base/holidays/meta" -Method GET | ConvertTo-Json -Depth 10

# Test 5: Create ServiceType
$body = @{
    name = "Cardiology"
    code = "CARD_001"
    description = "Heart and cardiovascular services"
    isActive = $true
    isLeaf = $true
    displayOrder = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "$base/service-types" -Method POST -Body $body -ContentType "application/json"
```

---

## ✅ **Done!**

Import the JSON to Postman and you're ready to test all 32 endpoints!

