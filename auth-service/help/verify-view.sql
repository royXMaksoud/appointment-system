-- ========================================
-- Quick Verification Script for v_user_permissions_v2
-- ========================================

-- 1. Check if view exists
SELECT 
    schemaname,
    viewname,
    viewowner,
    definition
FROM pg_views 
WHERE viewname = 'v_user_permissions_v2';

-- Expected: 1 row with view definition

-- ========================================
-- 2. Check view columns
SELECT 
    column_name,
    data_type,
    ordinal_position
FROM information_schema.columns 
WHERE table_name = 'v_user_permissions_v2'
ORDER BY ordinal_position;

-- Expected columns:
-- userId (uuid)
-- userName (text)
-- email (text)
-- systemId (uuid)
-- systemName (text)
-- system_icon (text)
-- systemSectionId (uuid)
-- systemSectionName (text)
-- systemSectionActionId (uuid)
-- actionName (text)
-- actionCode (text)
-- effect (text) ← NEW
-- codeTableId (uuid)
-- tableName (text)
-- levelIndex (integer) ← NEW
-- scopeValueId (uuid)
-- scopeValueName (text)
-- permissionType (text) ← NEW

-- ========================================
-- 3. Check if view has data
SELECT COUNT(*) as total_permissions 
FROM v_user_permissions_v2;

-- Expected: > 0 if there are any permissions

-- ========================================
-- 4. Check ACTION-level permissions
SELECT 
    "userName",
    "systemName",
    "systemSectionName",
    "actionName",
    "effect",
    "permissionType"
FROM v_user_permissions_v2
WHERE "permissionType" = 'ACTION'
LIMIT 5;

-- Expected: Permissions with permissionType = 'ACTION' and effect = 'ALLOW' or 'DENY'

-- ========================================
-- 5. Check SCOPE-level permissions
SELECT 
    "userName",
    "systemName",
    "systemSectionName",
    "actionName",
    "scopeValueName",
    "effect",
    "levelIndex",
    "permissionType"
FROM v_user_permissions_v2
WHERE "permissionType" = 'SCOPE'
LIMIT 5;

-- Expected: Permissions with permissionType = 'SCOPE', scopeValueName filled, and effect

-- ========================================
-- 6. Count permissions by type
SELECT 
    "permissionType",
    COUNT(*) as count
FROM v_user_permissions_v2
GROUP BY "permissionType";

-- Expected: 
-- ACTION | count
-- SCOPE  | count

-- ========================================
-- 7. Check permissions for a specific user (replace with actual user ID)
-- Replace 'YOUR-USER-ID-HERE' with actual UUID
/*
SELECT 
    "systemName",
    "systemSectionName",
    "actionName",
    "actionCode",
    "effect",
    "permissionType",
    "scopeValueName"
FROM v_user_permissions_v2
WHERE "userId" = 'YOUR-USER-ID-HERE'
ORDER BY "systemName", "systemSectionName", "actionName";
*/

-- ========================================
-- 8. Verify effect values are correct
SELECT DISTINCT "effect" 
FROM v_user_permissions_v2;

-- Expected: ALLOW, DENY, NONE (or subset)

-- ========================================
-- 9. Check for NULL values in critical fields
SELECT 
    COUNT(*) FILTER (WHERE "userId" IS NULL) as null_userId,
    COUNT(*) FILTER (WHERE "systemId" IS NULL) as null_systemId,
    COUNT(*) FILTER (WHERE "systemSectionActionId" IS NULL) as null_actionId,
    COUNT(*) FILTER (WHERE "effect" IS NULL) as null_effect,
    COUNT(*) FILTER (WHERE "permissionType" IS NULL) as null_permissionType
FROM v_user_permissions_v2;

-- Expected: All zeros (no NULLs in critical fields)

-- ========================================
-- 10. Sample data from view
SELECT 
    "userName",
    "systemName",
    "actionName",
    "effect",
    "scopeValueName",
    "permissionType"
FROM v_user_permissions_v2
LIMIT 10;

-- Expected: Mix of ACTION and SCOPE permissions with proper effects

-- ========================================
-- 11. Compare with old view (if it still exists)
/*
SELECT 
    'v_user_permissions_expanded' as view_name,
    COUNT(*) as record_count
FROM v_user_permissions_expanded
UNION ALL
SELECT 
    'v_user_permissions_v2' as view_name,
    COUNT(*) as record_count
FROM v_user_permissions_v2;
*/

-- ========================================
-- 12. Test query that auth-service will use
-- Replace with actual user ID
/*
SELECT
  v."userId", v."userName", v."email",
  v."systemId", v."systemName", v."system_icon",
  v."systemSectionId", v."systemSectionName",
  v."systemSectionActionId", v."actionName", v."actionCode",
  v."effect",
  v."codeTableId", v."tableName", v."levelIndex",
  v."scopeValueId", v."scopeValueName",
  v."permissionType"
FROM public.v_user_permissions_v2 v
WHERE v."userId" = 'YOUR-USER-ID-HERE'
ORDER BY v."systemName", v."systemSectionName", v."actionName";
*/

-- ========================================
-- TROUBLESHOOTING QUERIES
-- ========================================

-- If view is missing, check migration status
SELECT 
    installed_rank,
    version,
    description,
    script,
    success,
    execution_time
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- Check base tables have data
SELECT 'user_action_permissions' as table_name, COUNT(*) as count FROM user_action_permissions
UNION ALL
SELECT 'user_action_permission_nodes', COUNT(*) FROM user_action_permission_nodes
UNION ALL
SELECT 'action_scope_hierarchy', COUNT(*) FROM action_scope_hierarchy;

-- ========================================
-- FINAL CHECK: Run a sample permission query
-- ========================================
-- This simulates what the backend will execute
SELECT 
    "systemName",
    "systemSectionName",
    "actionName",
    "actionCode",
    "effect",
    "permissionType",
    COUNT(*) OVER() as total_permissions
FROM v_user_permissions_v2
LIMIT 1;

-- If this returns data, the view is working! ✅

