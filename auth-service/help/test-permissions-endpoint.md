# 🧪 Testing Permissions Endpoint

## Prerequisites
1. All services must be running:
   - ✅ Gateway Service (port 6060)
   - ✅ auth-service (port 6061)
   - ✅ access-management-service (port 6062)
2. Database migration applied (v_user_permissions_v2 view created)
3. Valid JWT token

---

## Step 1: Start All Services

### Terminal 1 - Gateway Service
```bash
cd C:\Java\care\Code\gateway-service
mvn spring-boot:run
```
Wait for: "Started GatewayServiceApplication"

### Terminal 2 - auth-service
```bash
cd C:\Java\care\Code\auth-service\auth-service
mvn clean compile
mvn spring-boot:run
```
Wait for: "Started AuthServiceApplication"

### Terminal 3 - access-management-service
```bash
cd C:\Java\care\Code\access-management-system\access-management-service\accessmanagement
mvn clean compile
mvn spring-boot:run
```
Wait for: "Started AccessManagementApplication"

---

## Step 2: Login to Get Token

```bash
curl -X POST http://localhost:6060/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@example.com",
    "password": "your-password"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

Copy the token value.

---

## Step 3: Test Permissions Endpoint

### Via Gateway (Recommended - as frontend uses it)
```bash
curl -X GET "http://localhost:6060/auth/me/permissions?force=false" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Direct to auth-service (For debugging)
```bash
curl -X GET "http://localhost:6061/auth/me/permissions?force=false" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Direct to access-management (For debugging)
```bash
curl -X GET "http://localhost:6062/api/permissions/users/YOUR_USER_ID" \
  -H "Accept: application/json"
```

---

## Expected Response Structure

```json
{
  "etag": "abc123...",
  "generatedAt": "2025-10-09T10:30:00Z",
  "systems": [
    {
      "systemId": "uuid",
      "name": "CMS",
      "sections": [
        {
          "systemSectionId": "uuid",
          "name": "Users",
          "actions": [
            {
              "systemSectionActionId": "uuid",
              "code": "CREATE_USER",
              "name": "Create User",
              "effect": "ALLOW",
              "scopes": []
            },
            {
              "systemSectionActionId": "uuid",
              "code": "EDIT_CONTENT",
              "name": "Edit Content",
              "effect": "NONE",
              "scopes": [
                {
                  "scopeValueId": "uuid",
                  "scopeValueName": "Syria",
                  "effect": "ALLOW",
                  "levelIndex": 0,
                  "codeTableId": "uuid",
                  "tableName": "countries"
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

---

## Verify New Fields Are Present

Check that response includes:
- ✅ `effect` field in each action
- ✅ `scopes` array in actions with scope-level permissions
- ✅ `permissionType` in raw data (if checking access-management directly)
- ✅ `levelIndex` in scope nodes

---

## Troubleshooting

### Issue 1: "Connection refused" on port 6062
**Solution:** access-management-service is not running
```bash
cd C:\Java\care\Code\access-management-system\access-management-service\accessmanagement
mvn spring-boot:run
```

### Issue 2: "View v_user_permissions_v2 does not exist"
**Solution:** Migration not applied
- Check Flyway logs in auth-service startup
- Manually run migration if needed:
```sql
-- Connect to database
psql -U postgres -d cms_db

-- Check if view exists
SELECT * FROM pg_views WHERE viewname = 'v_user_permissions_v2';

-- If not, run the migration file manually
\i C:/Java/care/Code/auth-service/auth-service/src/main/resources/db/migration/V2__create_user_permissions_view_v2.sql
```

### Issue 3: Missing fields in response
**Possible causes:**
- DTOs not updated properly
- Services not recompiled after changes

**Solution:**
```bash
# Restart all services with clean compile
mvn clean compile
mvn spring-boot:run
```

### Issue 4: "effect" always returns "NONE"
**Check:**
1. Data exists in `user_action_permissions` table
2. `action_effect` is set correctly
3. For scope-level: nodes exist in `user_action_permission_nodes`

```sql
-- Check permissions data
SELECT * FROM user_action_permissions WHERE user_id = 'YOUR_USER_ID';
SELECT * FROM user_action_permission_nodes WHERE user_action_permission_id = 'PERMISSION_ID';

-- Test the view directly
SELECT * FROM v_user_permissions_v2 WHERE "userId" = 'YOUR_USER_ID' LIMIT 10;
```

---

## Quick Database Verification

### Check if view exists and has data:
```sql
-- Connect to database
psql -U postgres -d cms_db

-- Check view exists
SELECT * FROM pg_views WHERE viewname = 'v_user_permissions_v2';

-- Check view has data
SELECT COUNT(*) FROM v_user_permissions_v2;

-- Check your user's permissions
SELECT * FROM v_user_permissions_v2 
WHERE "userId" = 'YOUR_USER_ID' 
LIMIT 5;

-- Verify columns
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'v_user_permissions_v2';
```

---

## Success Indicators

✅ **All checks passed if:**
1. All 3 services start without errors
2. Login returns valid JWT token
3. `/auth/me/permissions` returns 200 OK
4. Response contains `systems` array
5. Each action has `effect` field
6. Actions with scopes show `scopes` array
7. Frontend HomeCare page shows systems

---

## Performance Check

### Check response time:
```bash
time curl -X GET "http://localhost:6060/auth/me/permissions?force=false" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -s -o /dev/null -w "%{time_total}\n"
```

**Expected:** < 2 seconds on first call, < 0.5 seconds on cached calls

### Check ETag caching:
```bash
# First call - returns full data
curl -v "http://localhost:6060/auth/me/permissions?force=false" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  2>&1 | grep -i etag

# Copy ETag value, then:
# Second call with If-None-Match - should return 304
curl -v "http://localhost:6060/auth/me/permissions?force=false" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "If-None-Match: YOUR_ETAG_VALUE" \
  2>&1 | grep "HTTP/"
```

**Expected:** Second call returns `304 Not Modified`

---

## Frontend Integration Test

1. Start frontend:
```bash
cd C:\Java\care\Code\web-portal
npm run dev
```

2. Open browser: `http://localhost:5173`
3. Login
4. Should redirect to home page
5. Check browser console (F12):
   - Network tab → `/auth/me/permissions` → Response
   - Should see new structure with `effect` and `scopes`

6. Check HomeCare page:
   - Should only show systems you have access to
   - Should show permission counts (e.g., "5/10 actions")
   - Should show icons (✓ for full access, 🔒 for partial)

---

## Summary

✅ **Everything is connected correctly:**
- Gateway routes to auth-service
- auth-service calls access-management
- access-management queries v_user_permissions_v2
- Response structure matches new DTOs
- Frontend utilities work with new structure

