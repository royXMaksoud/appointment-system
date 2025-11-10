# ✅ Permissions System Integration Verification

## System Architecture

### 1. Frontend → Gateway → auth-service
```
Frontend (port 5173)
    ↓ GET /auth/me/permissions
Gateway (port 6060)
    ↓
MeController.java (port 6061) ✅
```

### 2. auth-service → access-management-service
```
MeController.java
    ↓ calls
PermissionAggregationService.java ✅ UPDATED
    ↓ calls
PermissionClient.java ✅
    ↓ HTTP GET
access-management-service (port 6062)
    /api/permissions/users/{userId}
```

### 3. access-management-service → Database
```
PermissionQueryController.java
    ↓ calls
GetUserPermissionsUseCase
    ↓ calls
UserPermissionQueryRepository.java ✅ UPDATED
    ↓ SQL query
v_user_permissions_v2 ✅ NEW VIEW
```

---

## ✅ Verification Checklist

### Database Layer ✅
- [x] `v_user_permissions_v2` created
- [x] Supports ACTION-level permissions (no scopes)
- [x] Supports SCOPE-level permissions (with nodes)
- [x] Returns `effect`, `permissionType`, `levelIndex`

### Backend DTOs ✅
- [x] `PermissionRow` - updated with new fields
- [x] `ScopeNodeDTO` - created for scope permissions
- [x] `ActionDTO` - updated with effect and scopes
- [x] `PermissionTree` - unchanged (works with new structure)

### access-management-service ✅
- [x] `UserPermissionQueryRepository` - uses `v_user_permissions_v2`
- [x] Query includes: effect, permissionType, levelIndex, scopeValueId
- [x] Returns data in new format

### auth-service ✅
- [x] `PermissionClient` - fetches from access-management
- [x] `PermissionAggregationService.buildTree()` - builds tree with effects
- [x] `PermissionAggregationService.toTree()` - processes ACTION vs SCOPE types
- [x] `MeController.myPermissions()` - returns PermissionTree
- [x] `AuthController.login()` - warms up cache

### Frontend ✅
- [x] `fetchMyPermissions()` - calls /auth/me/permissions
- [x] `permissions.js` - utility functions
- [x] `HomeCare.jsx` - displays systems with permissions
- [x] `UserPermissionsTab.jsx` - manages permissions

---

## 🔍 How It Works

### Step 1: Login
```java
// AuthController.java (line 69-104)
@PostMapping("/login")
public JwtResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
    User user = loginUseCase.login(request.getEmail(), request.getPassword());
    
    // Check permissions (optional)
    boolean hasAccess = permissionAggregationService.hasAnyAccess(user.getId());
    
    // Generate JWT
    String token = jwtTokenProvider.generateToken(...);
    
    // Warm up cache (loads permissions in background)
    permissionAggregationService.reloadUserTree(user.getId());
    
    return new JwtResponseDTO(token);
}
```

### Step 2: Get Permissions
```java
// MeController.java (line 21-44)
@GetMapping("/permissions")
public ResponseEntity<PermissionTree> myPermissions(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(value = "force", defaultValue = "false") boolean force
) {
    UUID userId = jwtTokenProvider.getUserIdFromToken(token);
    
    // Load from cache or reload
    PermissionTree tree = force
        ? permissionAggregationService.reloadUserTree(userId)
        : permissionAggregationService.loadAndCacheUserTree(userId);
    
    // ETag support for caching
    if (etag matches) return 304;
    
    return ResponseEntity.ok().eTag(tree.etag()).body(tree);
}
```

### Step 3: Build Permission Tree
```java
// PermissionAggregationService.java
private PermissionTree buildTree(UUID userId) {
    // 1. Fetch from access-management-service
    var rows = client.fetchUserPermissionsPage(userId, ...);
    
    // 2. Process rows into tree structure
    List<SystemDTO> systems = toTree(rows);
    
    // 3. Compute ETag for caching
    String etag = computeEtag(rows);
    
    return new PermissionTree(etag, Instant.now(), systems);
}

private static List<SystemDTO> toTree(List<PermissionRow> rows) {
    // Groups by system → section → action
    
    for each action:
        // Check permission type
        if (permissionType == "ACTION"):
            // Action-level permission
            effect = row.effect()
            scopes = []
        else:
            // Scope-level permission
            scopes = build from rows with matching actionId
            effect = "NONE" (at action level)
        
        return new ActionDTO(id, code, name, effect, scopes)
}
```

### Step 4: Query Database
```sql
-- UserPermissionQueryRepository.java
SELECT
  v."userId", v."userName", v."email",
  v."systemId", v."systemName", v."system_icon",
  v."systemSectionId", v."systemSectionName",
  v."systemSectionActionId", v."actionName", v."actionCode",
  v."effect",                    -- ✅ NEW
  v."codeTableId", v."tableName", v."levelIndex",  -- ✅ NEW
  v."scopeValueId", v."scopeValueName",
  v."permissionType"             -- ✅ NEW
FROM public.v_user_permissions_v2 v
WHERE v."userId" = :userId
```

### Step 5: View Definition
```sql
-- v_user_permissions_v2
-- Part 1: SCOPE-level permissions
SELECT 
    uap.user_id, ...,
    uapn.effect AS "effect",
    ash.order_index AS "levelIndex",
    uapn.code_table_value_id AS "scopeValueId",
    'SCOPE' AS "permissionType"
FROM user_action_permissions uap
JOIN user_action_permission_nodes uapn ON ...
WHERE uapn.user_action_permission_node_id IS NOT NULL

UNION ALL

-- Part 2: ACTION-level permissions (no scopes)
SELECT 
    uap.user_id, ...,
    uap.action_effect::text AS "effect",
    NULL AS "levelIndex",
    NULL AS "scopeValueId",
    'ACTION' AS "permissionType"
FROM user_action_permissions uap
WHERE NOT EXISTS (
    SELECT 1 FROM action_scope_hierarchy 
    WHERE system_section_action_id = ssa.system_section_action_id
)
```

---

## 🎯 Response Structure

### From Database (PermissionRow)
```json
{
  "userId": "uuid",
  "systemId": "uuid",
  "systemSectionActionId": "uuid",
  "actionCode": "CREATE_USER",
  "effect": "ALLOW",              // ✅ NEW
  "permissionType": "ACTION",     // ✅ NEW
  "scopeValueId": null,
  "levelIndex": null
}
```

### After Processing (PermissionTree)
```json
{
  "etag": "abc123...",
  "generatedAt": "2025-10-09T...",
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
              "effect": "ALLOW",        // ✅ NEW
              "scopes": []              // ✅ NEW
            },
            {
              "systemSectionActionId": "uuid",
              "code": "EDIT_CONTENT",
              "name": "Edit Content",
              "effect": "NONE",
              "scopes": [               // ✅ NEW
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

## 🔄 Caching Strategy

### Spring Cache (Backend)
```java
@Cacheable(cacheNames = "permissions", key = "#userId")
public PermissionTree loadAndCacheUserTree(UUID userId)

@CachePut(cacheNames = "permissions", key = "#userId")
public PermissionTree reloadUserTree(UUID userId)

@CacheEvict(cacheNames = "permissions", key = "#userId")
public void evictUserPermissions(UUID userId)
```

### ETag (HTTP)
```
Request:
  GET /auth/me/permissions
  If-None-Match: "abc123..."

Response (if not modified):
  304 Not Modified
  ETag: "abc123..."

Response (if modified):
  200 OK
  ETag: "xyz789..."
  Body: { ... }
```

### React Query (Frontend)
```javascript
useQuery({
  queryKey: ['me', 'permissions'],
  queryFn: fetchMyPermissions,
  staleTime: 5 * 60 * 1000  // 5 minutes
})
```

---

## ✅ Everything is Connected!

1. **MeController** ✅
   - Uses `PermissionAggregationService`
   - Returns `PermissionTree` with new structure
   - Supports ETag caching

2. **AuthController** ✅
   - Checks permissions on login
   - Warms up cache after login
   - Uses same `PermissionAggregationService`

3. **PermissionAggregationService** ✅
   - Fetches from access-management via `PermissionClient`
   - Processes new `PermissionRow` structure
   - Builds `ActionDTO` with `effect` and `scopes`
   - Generates ETag from all fields

4. **PermissionClient** ✅
   - Calls `/api/permissions/users/{userId}`
   - Maps response to `PermissionRow`
   - Handles pagination

5. **UserPermissionQueryRepository** ✅
   - Queries `v_user_permissions_v2`
   - Returns new fields: effect, permissionType, levelIndex

6. **v_user_permissions_v2** ✅
   - Replaces old `v_user_permissions_expanded`
   - Supports ACTION and SCOPE types
   - Returns all required fields

---

## 🚀 No Changes Needed to Controllers!

The controllers **already work** with the new system because:

1. They use `PermissionAggregationService` (not direct DB access)
2. `PermissionAggregationService` was updated to handle new structure
3. `PermissionTree` response format supports both old and new data
4. Backend changes are transparent to the controllers

**Everything flows automatically through the updated chain!** ✅

