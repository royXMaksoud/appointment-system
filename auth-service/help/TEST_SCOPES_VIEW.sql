-- =====================================================
-- Test Script for v_user_permissions_v2 View
-- =====================================================

-- 1. Check if view exists
SELECT 
    schemaname,
    viewname,
    viewowner
FROM pg_views 
WHERE viewname = 'v_user_permissions_v2';

-- Expected: 1 row showing the view exists

-- =====================================================
-- 2. Count records by permission type
-- =====================================================
SELECT 
    "permissionType",
    COUNT(*) as total_records
FROM v_user_permissions_v2
GROUP BY "permissionType"
ORDER BY "permissionType";

-- Expected:
-- permissionType | total_records
-- ---------------|---------------
-- ACTION         | X
-- SCOPE          | Y

-- =====================================================
-- 3. Sample SCOPE permissions (الأهم!)
-- =====================================================
SELECT 
    "userName",
    "systemName",
    "systemSectionName",
    "actionName",
    "actionCode",
    "effect",
    "scopeValueName",
    "levelIndex",
    "tableName",
    "codeTableId",
    "scopeValueId"
FROM v_user_permissions_v2
WHERE "permissionType" = 'SCOPE'
ORDER BY "userName", "systemName", "actionName", "levelIndex", "scopeValueName"
LIMIT 20;

-- Expected: Rows showing Female, Male, Syria, Jordan, etc.
-- Must have:
-- - scopeValueName NOT NULL
-- - codeTableId NOT NULL  
-- - tableName NOT NULL (like 'Gender', 'Country')
-- - levelIndex (0, 1, 2...)
-- - effect (ALLOW or DENY)

-- =====================================================
-- 4. Sample ACTION permissions
-- =====================================================
SELECT 
    "userName",
    "systemName",
    "systemSectionName",
    "actionName",
    "actionCode",
    "effect",
    "codeTableId",
    "scopeValueId",
    "tableName"
FROM v_user_permissions_v2
WHERE "permissionType" = 'ACTION'
ORDER BY "userName", "systemName", "actionName"
LIMIT 10;

-- Expected: Rows with:
-- - effect (ALLOW or DENY)
-- - codeTableId IS NULL
-- - scopeValueId IS NULL
-- - tableName IS NULL

-- =====================================================
-- 5. Check for specific user (replace UUID)
-- =====================================================
-- Replace 'YOUR_USER_UUID' with actual user ID
SELECT 
    "permissionType",
    "systemName",
    "actionName",
    "effect",
    "scopeValueName",
    "levelIndex"
FROM v_user_permissions_v2
WHERE "userId" = '2a0e16f3-5c9d-4856-9d8c-234bc7ea7e40'  -- ✏️ Change this!
ORDER BY "systemName", "actionName", "levelIndex", "scopeValueName";

-- =====================================================
-- 6. Check for actions with both ACTION and SCOPE
-- =====================================================
-- Find actions that have both permission types
WITH action_types AS (
    SELECT 
        "systemSectionActionId",
        "actionName",
        "permissionType",
        COUNT(*) as count
    FROM v_user_permissions_v2
    GROUP BY "systemSectionActionId", "actionName", "permissionType"
),
actions_with_both AS (
    SELECT 
        "systemSectionActionId",
        "actionName"
    FROM action_types
    GROUP BY "systemSectionActionId", "actionName"
    HAVING COUNT(DISTINCT "permissionType") = 2
)
SELECT 
    v."systemName",
    v."actionName",
    v."permissionType",
    v."effect",
    v."scopeValueName"
FROM v_user_permissions_v2 v
INNER JOIN actions_with_both awb 
    ON v."systemSectionActionId" = awb."systemSectionActionId"
ORDER BY v."systemName", v."actionName", v."permissionType", v."scopeValueName";

-- Expected: Actions that have BOTH ACTION-level and SCOPE-level permissions

-- =====================================================
-- 7. Verify code_table_id is populated for SCOPE
-- =====================================================
SELECT 
    COUNT(*) as total_scope_records,
    COUNT("codeTableId") as has_code_table_id,
    COUNT("tableName") as has_table_name,
    COUNT("scopeValueId") as has_scope_value_id,
    COUNT("scopeValueName") as has_scope_value_name
FROM v_user_permissions_v2
WHERE "permissionType" = 'SCOPE';

-- Expected: All counts should be equal (no NULLs in SCOPE records)

-- =====================================================
-- 8. Check levelIndex distribution
-- =====================================================
SELECT 
    "levelIndex",
    COUNT(*) as count,
    STRING_AGG(DISTINCT "tableName", ', ') as tables
FROM v_user_permissions_v2
WHERE "permissionType" = 'SCOPE'
GROUP BY "levelIndex"
ORDER BY "levelIndex" NULLS FIRST;

-- Expected:
-- levelIndex | count | tables
-- -----------|-------|----------------
-- 0          | X     | Gender, Country
-- 1          | Y     | City, Province
-- (null)     | Z     | (if any)

-- =====================================================
-- 9. Find permissions by system
-- =====================================================
SELECT 
    "systemName",
    "permissionType",
    COUNT(*) as permission_count
FROM v_user_permissions_v2
GROUP BY "systemName", "permissionType"
ORDER BY "systemName", "permissionType";

-- Expected: Distribution of ACTION vs SCOPE by system

-- =====================================================
-- 10. Detailed view for debugging
-- =====================================================
-- Full record view for a specific action (replace action code)
SELECT *
FROM v_user_permissions_v2
WHERE "actionCode" = 'CMS_CONTENT_CREATE'  -- ✏️ Change this to your action code
ORDER BY "permissionType", "scopeValueName";

-- =====================================================
-- VALIDATION CHECKLIST
-- =====================================================
/*
✅ View exists
✅ Both ACTION and SCOPE records present
✅ SCOPE records have:
   - scopeValueName (Female, Male, etc.)
   - codeTableId (not null)
   - tableName (Gender, Country, etc.)
   - levelIndex (0, 1, 2...)
   - effect (ALLOW or DENY)
✅ ACTION records have:
   - effect (ALLOW or DENY)
   - NULL for: codeTableId, scopeValueId, tableName, levelIndex
✅ No unexpected NULLs in critical fields
✅ Data matches user_action_permission_nodes table
*/

-- =====================================================
-- Compare with source table
-- =====================================================
-- Verify that view data matches user_action_permission_nodes
SELECT 
    uapn.effect,
    uapn.code_table_value_id,
    ctv.name as scope_name,
    ctv.code_table_id,
    ct.name as table_name,
    ash.order_index as level_index,
    ssa.name as action_name,
    ssa.code as action_code
FROM user_action_permission_nodes uapn
JOIN user_action_permissions uap 
    ON uap.user_action_permission_id = uapn.user_action_permission_id
JOIN system_section_actions ssa 
    ON ssa.system_section_action_id = uap.system_section_action_id
JOIN code_table_value ctv 
    ON ctv.code_table_value_id = uapn.code_table_value_id
LEFT JOIN action_scope_hierarchy ash 
    ON ash.system_section_action_id = ssa.system_section_action_id 
    AND ash.code_table_id = ctv.code_table_id
LEFT JOIN code_table ct 
    ON ct.code_table_id = ctv.code_table_id
WHERE uap.is_active = true 
  AND uap.is_deleted = false
LIMIT 20;

-- Compare this with SCOPE records from the view - they should match!

