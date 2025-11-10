#!/bin/bash
# =====================================================
# Test Script for /auth/me/permissions API
# =====================================================

# Configuration
BASE_URL="http://localhost:6060"
AUTH_ENDPOINT="/auth/login"
PERMS_ENDPOINT="/auth/me/permissions"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "======================================================"
echo "🧪 Testing Scopes in /auth/me/permissions API"
echo "======================================================"

# Step 1: Login (replace with your credentials)
echo ""
echo "${YELLOW}Step 1: Logging in...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}${AUTH_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@example.com",
    "password": "your-password"
  }')

# Extract token (adjust based on your response structure)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "${RED}❌ Login failed! Check credentials.${NC}"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

echo "${GREEN}✅ Login successful!${NC}"
echo "Token: ${TOKEN:0:20}..."

# Step 2: Fetch permissions WITHOUT force (from cache)
echo ""
echo "${YELLOW}Step 2: Fetching permissions (cached)...${NC}"
PERMS_CACHED=$(curl -s "${BASE_URL}${PERMS_ENDPOINT}?force=false" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json")

echo "Cached response length: $(echo $PERMS_CACHED | wc -c) bytes"

# Step 3: Fetch permissions WITH force (fresh from DB)
echo ""
echo "${YELLOW}Step 3: Fetching permissions (force refresh)...${NC}"
PERMS_FRESH=$(curl -s "${BASE_URL}${PERMS_ENDPOINT}?force=true" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json")

# Save to file for inspection
echo "$PERMS_FRESH" > permissions_response.json
echo "${GREEN}✅ Saved full response to: permissions_response.json${NC}"

# Step 4: Check if scopes are present
echo ""
echo "${YELLOW}Step 4: Analyzing scopes...${NC}"

# Count total actions
TOTAL_ACTIONS=$(echo $PERMS_FRESH | grep -o '"systemSectionActionId"' | wc -l)
echo "Total actions found: $TOTAL_ACTIONS"

# Check if any action has scopes
HAS_SCOPES=$(echo $PERMS_FRESH | grep -o '"scopes":\[{' | wc -l)

if [ $HAS_SCOPES -gt 0 ]; then
    echo "${GREEN}✅ SUCCESS: Found $HAS_SCOPES actions with scopes!${NC}"
else
    # Check if scopes array exists but empty
    EMPTY_SCOPES=$(echo $PERMS_FRESH | grep -o '"scopes":\[\]' | wc -l)
    if [ $EMPTY_SCOPES -eq $TOTAL_ACTIONS ]; then
        echo "${RED}❌ FAIL: All scopes arrays are empty!${NC}"
        echo "This means the backend is not populating scopes correctly."
    else
        echo "${YELLOW}⚠️  WARNING: Mixed results - some actions have scopes, some don't.${NC}"
    fi
fi

# Step 5: Extract and display sample scopes
echo ""
echo "${YELLOW}Step 5: Sample scopes from response:${NC}"
echo "-------------------------------------------------------"

# Use jq if available, otherwise use grep
if command -v jq &> /dev/null; then
    echo "$PERMS_FRESH" | jq '.systems[].sections[].actions[] | select(.scopes | length > 0) | {actionName: .name, effect: .effect, scopes: .scopes}' 2>/dev/null | head -50
    
    # Count scopes per action
    echo ""
    echo "${YELLOW}Scopes count per action:${NC}"
    echo "$PERMS_FRESH" | jq '.systems[].sections[].actions[] | {action: .name, scopeCount: (.scopes | length)}' 2>/dev/null | grep -v '"scopeCount": 0'
else
    echo "${YELLOW}⚠️  jq not installed - showing raw grep results:${NC}"
    echo "$PERMS_FRESH" | grep -A 10 '"scopes":\[{' | head -30
fi

echo ""
echo "======================================================"
echo "📊 Test Summary"
echo "======================================================"
echo "Total actions: $TOTAL_ACTIONS"
echo "Actions with scopes: $HAS_SCOPES"

if [ $HAS_SCOPES -gt 0 ]; then
    echo "${GREEN}Status: ✅ PASS - Scopes are working!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Check permissions_response.json for full details"
    echo "2. Verify in frontend (HomeCare.jsx, UserPermissionsTab.jsx)"
    echo "3. Test permission checking logic"
else
    echo "${RED}Status: ❌ FAIL - Scopes are not populated!${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Check v_user_permissions_v2 view in database:"
    echo "   SELECT * FROM v_user_permissions_v2 WHERE \"permissionType\" = 'SCOPE' LIMIT 5;"
    echo "2. Verify user_action_permission_nodes has data"
    echo "3. Check PermissionAggregationService.java logs"
    echo "4. Restart auth-service and try again with ?force=true"
fi

echo ""
echo "Full response saved in: permissions_response.json"
echo "======================================================"

