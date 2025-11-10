#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

API_URL="http://localhost:6060/appointment/api/admin/schedules"
TOKEN="YOUR_JWT_TOKEN_HERE"  # You'll need to get this from /auth/login

echo -e "${YELLOW}=== Testing Schedule Filter API ===${NC}\n"

# Test 1: Get all schedules without filter
echo -e "${YELLOW}Test 1: Get ALL schedules (no filter)${NC}"
curl -X POST "$API_URL/filter" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' \
  -w "\nStatus: %{http_code}\n\n"

echo -e "\n${YELLOW}Test 2: Filter with IN operator (scope values)${NC}"
curl -X POST "$API_URL/filter" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "criteria": [{
      "key": "organizationBranchId",
      "operator": "IN",
      "value": ["6240dfac-e4ac-4a29-86a4-7a7f29553c17", "7df356fb-f1db-4075-a31b-ba20bc5aad15"],
      "dataType": "UUID"
    }]
  }' \
  -w "\nStatus: %{http_code}\n\n"

echo -e "\n${YELLOW}Test 3: Simple EQUAL filter${NC}"
curl -X POST "$API_URL/filter" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "criteria": [{
      "key": "organizationBranchId",
      "operator": "EQUAL",
      "value": "6240dfac-e4ac-4a29-86a4-7a7f29553c17",
      "dataType": "UUID"
    }]
  }' \
  -w "\nStatus: %{http_code}\n\n"

echo -e "\n${YELLOW}Test 4: Test dayOfWeek filter${NC}"
curl -X POST "$API_URL/filter" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "criteria": [{
      "key": "dayOfWeek",
      "operator": "EQUAL",
      "value": 1,
      "dataType": "NUMBER"
    }]
  }' \
  -w "\nStatus: %{http_code}\n\n"

echo -e "${GREEN}=== Tests Complete ===${NC}"
