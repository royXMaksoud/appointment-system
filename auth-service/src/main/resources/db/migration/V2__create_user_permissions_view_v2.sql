-- Create new view for user permissions using the new tables
-- DROP VIEW IF EXISTS public.v_user_permissions_v2 CASCADE;

CREATE OR REPLACE VIEW public.v_user_permissions_v2 AS
-- Actions with scopes (from user_action_permission_nodes)
SELECT 
    uap.user_id AS "userId",
    u.full_name AS "userName",
    u.email AS "email",
    s.system_id AS "systemId",
    s.name AS "systemName",
    s.system_icon,
    ss.system_section_id AS "systemSectionId",
    ss.name AS "systemSectionName",
    ssa.system_section_action_id AS "systemSectionActionId",
    ssa.name AS "actionName",
    ssa.code AS "actionCode",
    uapn.effect AS "effect",
    ctv.code_table_id AS "codeTableId",
    ct.name AS "tableName",
    ash.order_index AS "levelIndex",
    uapn.code_table_value_id AS "scopeValueId",
    ctv.name AS "scopeValueName",
    'SCOPE' AS "permissionType"
FROM user_action_permissions uap
JOIN system_section_actions ssa ON ssa.system_section_action_id = uap.system_section_action_id
JOIN system_sections ss ON ss.system_section_id = ssa.system_section_id
JOIN systems s ON s.system_id = ss.system_id
JOIN user_action_permission_nodes uapn ON uapn.user_action_permission_id = uap.user_action_permission_id
JOIN code_table_value ctv ON ctv.code_table_value_id = uapn.code_table_value_id
LEFT JOIN users u ON u.id = uap.user_id
LEFT JOIN action_scope_hierarchy ash ON ash.system_section_action_id = ssa.system_section_action_id 
    AND ash.code_table_id = ctv.code_table_id
LEFT JOIN code_tables ct ON ct.code_table_id = ctv.code_table_id
WHERE uap.is_active = true 
  AND uap.is_deleted = false

UNION ALL

-- Actions without scopes (action-level permissions only)
SELECT 
    uap.user_id AS "userId",
    u.full_name AS "userName",
    u.email AS "email",
    s.system_id AS "systemId",
    s.name AS "systemName",
    s.system_icon,
    ss.system_section_id AS "systemSectionId",
    ss.name AS "systemSectionName",
    ssa.system_section_action_id AS "systemSectionActionId",
    ssa.name AS "actionName",
    ssa.code AS "actionCode",
    uap.action_effect::text AS "effect",
    NULL::uuid AS "codeTableId",
    NULL::text AS "tableName",
    NULL::integer AS "levelIndex",
    NULL::uuid AS "scopeValueId",
    NULL::text AS "scopeValueName",
    'ACTION' AS "permissionType"
FROM user_action_permissions uap
JOIN system_section_actions ssa ON ssa.system_section_action_id = uap.system_section_action_id
JOIN system_sections ss ON ss.system_section_id = ssa.system_section_id
JOIN systems s ON s.system_id = ss.system_id
LEFT JOIN users u ON u.id = uap.user_id
WHERE uap.is_active = true 
  AND uap.is_deleted = false
  AND NOT EXISTS (
      SELECT 1 
      FROM action_scope_hierarchy ash 
      WHERE ash.system_section_action_id = ssa.system_section_action_id
  );

COMMENT ON VIEW public.v_user_permissions_v2 IS 
'User permissions view with support for both action-level and scope-level permissions with ALLOW/DENY/NONE effects';

