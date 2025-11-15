# Permission Control System on Pages - PermissionGrantOnPage

## Overview
This document explains how to control user permissions at the page level for CRUD operations (Create, Read, Update, Delete) on DataTable and UI elements.

## System Architecture

### Permission Hierarchy
```
Systems (e.g., "Appointments")
  ├─ Sections (e.g., "Appointment Scheduling and Calendars")
  │   ├─ Actions (e.g., "List", "Create", "Update", "Delete")
  │   │   ├─ Effect: "ALLOW", "DENY", or "NONE"
  │   │   └─ Scopes (optional branch/center restrictions)
```

---

## Key Components & Hooks

### 1. **PermissionsContext** (`src/contexts/PermissionsContext.jsx`)
- **Purpose**: Global provider for user permissions data
- **Provides**:
  - `permissionsData`: Full permission structure from API
  - `hasPermission(actionCode, sectionName, scopeValueId)`: Check single permission
  - `getSectionPermissions(sectionName)`: Get all CRUD permissions for a section
  - `getAccessibleSections()`: Get all sections user can access
  - `refreshPermissions()`: Force refresh permissions

**Usage**:
```javascript
const { hasPermission, getSectionPermissions } = usePermissionCheck()

// Check specific action
const canCreate = hasPermission('Create', 'Appointment Scheduling')

// Get all permissions for a section
const perms = getSectionPermissions('Appointment Scheduling')
// Returns: { canCreate, canList, canUpdate, canDelete, actions }
```

### 2. **useSystemSectionScopes Hook** (`src/modules/appointment/hooks/useSystemSectionScopes.js`)
- **Purpose**: Extract scoped access (branch/center restrictions) for a specific section
- **Returns**:
  - `scopeValueIds`: Array of UUIDs user can access (e.g., branch IDs)
  - `hasAccess`: Boolean - whether user has any access to this section
  - `isGlobalAccess`: Boolean - whether access is global (not scoped to specific branches)

**Usage**:
```javascript
const { scopeValueIds, hasAccess } = useSystemSectionScopes(SYSTEM_SECTIONS.APPOINTMENT_SCHEDULING)

// scopeValueIds = ['6240dfac-e4ac-4a29-86a4-7a7f29553c17', '7df356fb-f1db-4075-a31b-ba20bc5aad15']
// These are the branch IDs the user can access
```

---

## Implementation Pattern: Permission-Controlled DataTable Page

### Step 1: Import Required Dependencies
```javascript
import { SYSTEM_SECTIONS } from '@/config/systemSectionConstants'
import { useSystemSectionScopes } from '@/modules/appointment/hooks/useSystemSectionScopes'
import { usePermissionCheck } from '@/contexts/PermissionsContext'
```

### Step 2: Extract Scopes for Section
```javascript
export default function AppointmentList() {
  // Get branch IDs the user can access for this section
  const { scopeValueIds } = useSystemSectionScopes(SYSTEM_SECTIONS.APPOINTMENT_SCHEDULING)

  // Use scopeValueIds to filter API calls
  useEffect(() => {
    if (scopeValueIds.length > 0) {
      // Fetch data only for authorized branches
      fetchAppointments({ branchIds: scopeValueIds })
    }
  }, [scopeValueIds])
}
```

### Step 3: Control CREATE Button Visibility
```javascript
const { hasPermission } = usePermissionCheck()

// Check if user can create appointments
const canCreate = hasPermission('Create', 'Appointment Scheduling and Calendars')

return (
  <>
    {canCreate && (
      <button onClick={handleOpenCreate}>
        + Add Appointment
      </button>
    )}
  </>
)
```

### Step 4: Control Row Actions (Update/Delete)
```javascript
const columns = [
  {
    id: 'actions',
    header: 'Actions',
    cell: ({ row }) => {
      const canEdit = hasPermission('Update', 'Appointment Scheduling and Calendars')
      const canDelete = hasPermission('Delete', 'Appointment Scheduling and Calendars')

      return (
        <div className="flex gap-2">
          {canEdit && (
            <button onClick={() => handleEdit(row.original)}>Edit</button>
          )}
          {canDelete && (
            <button onClick={() => handleDelete(row.original)}>Delete</button>
          )}
        </div>
      )
    },
  },
]
```

### Step 5: Filter Data Sent to API
```javascript
// When user filters or searches, include scopeValueIds
const handleFilterChange = async (filters) => {
  const payload = {
    ...filters,
    // Always include authorized branches in API request
    criteria: [
      {
        key: 'organizationBranchId',
        operator: 'IN',
        value: scopeValueIds,
        dataType: 'UUID'
      }
    ]
  }

  const results = await api.post('/appointments/filter', payload)
}
```

---

## Real-World Examples

### Example 1: AppointmentList.jsx
**Section**: APPOINTMENT_SCHEDULING
**Scopes**: Specific branch IDs (e.g., ["Alepp SARC", "Branch DAM DASTN"])

```javascript
const { scopeValueIds } = useSystemSectionScopes(SYSTEM_SECTIONS.APPOINTMENT_SCHEDULING)

// Controls:
// - CREATE: Shows "Add Appointment" button if hasPermission('Create')
// - READ: Only shows appointments from authorized branches (filtered by scopeValueIds)
// - UPDATE: Shows edit button only if hasPermission('Update')
// - DELETE: Shows delete button only if hasPermission('Delete')
```

### Example 2: BranchServiceTypeList.jsx
**Section**: Appointment_SETUP_AND_CONFIGURATION
**Scopes**: Specific branch IDs

```javascript
const { scopeValueIds } = useSystemSectionScopes(SYSTEM_SECTIONS.Appointment_SETUP_AND_CONFIGURATION)

// Call API with scoped filter
const loadScopedBranches = async () => {
  const response = await api.post(
    '/access/api/dropdowns/organization-branches/by-scope',
    { scopeValueIds } // Only branches user can configure
  )
}
```

### Example 3: ServiceTypeList.jsx
**Section**: Appointment_Reference_Data
**Scopes**: Global (NO scope restrictions - effect: ALLOW with empty scopes)

```javascript
const { scopeValueIds, hasAccess, isGlobalAccess } = useSystemSectionScopes(
  SYSTEM_SECTIONS.Appointment_Reference_Data
)

// If isGlobalAccess = true, user can access ALL service types
// If isGlobalAccess = false, scopeValueIds contains branch restrictions
```

---

## Two Access Patterns

### Pattern 1: Scope-Based Access (Branch-Level)
Used when user can only access specific branches/centers

**Sections**:
- APPOINTMENT_SCHEDULING
- APPOINTMENT_REPORTING_AND_ANALYTICS
- Appointment_SETUP_AND_CONFIGURATION

**Example Effect**:
```json
{
  "effect": "ALLOW",
  "scopes": [
    {"scopeValueId": "branch-id-1", "scopeValueName": "Alepp SARC", "effect": "ALLOW"},
    {"scopeValueId": "branch-id-2", "scopeValueName": "SARC Zahehra", "effect": "ALLOW"}
  ]
}
```

**Usage**: Filter DataTable rows to show only authorized branches

---

### Pattern 2: Global Access (System-Level)
Used when user can access entire system without branch restrictions

**Sections**:
- Appointment_USERS
- Appointment_Reference_Data

**Example Effect**:
```json
{
  "effect": "ALLOW",
  "scopes": []  // No scope restrictions - full access
}
```

**Usage**: Show all data without branch filtering

---

## Permission Control Checklist

### On Page Load:
- [ ] Extract scopeValueIds for the page's section
- [ ] Check if user has `hasAccess` to the section
- [ ] If no access, show "Access Denied" message
- [ ] Load all lookup data (organizations, branches, etc.)

### When Rendering Table:
- [ ] Filter rows by scopeValueIds (scope-based access only)
- [ ] Show/hide CREATE button based on `hasPermission('Create')`
- [ ] For each row, conditionally show UPDATE/DELETE buttons

### When User Takes Action:
- [ ] Include scopeValueIds in API request (authorization filter)
- [ ] Validate result matches authorized scopes
- [ ] Show success/error toast notification

---

## Common Pitfalls

### ❌ Mistake 1: Forgetting scopeValueIds in API Call
```javascript
// WRONG - sends unfiltered request
await api.get('/appointments')

// CORRECT - includes authorized branch filter
await api.post('/appointments/filter', {
  criteria: [{
    key: 'organizationBranchId',
    operator: 'IN',
    value: scopeValueIds,
    dataType: 'UUID'
  }]
})
```

### ❌ Mistake 2: Only Checking Button Visibility
```javascript
// WRONG - UI hides button but backend allows request
if (canCreate) {
  <button>Create</button>  // Only hiding button, backend still creates!
}

// CORRECT - also validate scope in API call
const canCreate = hasPermission('Create')
const inAuthorizedScope = scopeValueIds.includes(selectedBranchId)

if (canCreate && inAuthorizedScope) {
  <button>Create</button>
}
```

### ❌ Mistake 3: Infinite Loop with scopeValueIds
```javascript
// WRONG - creates new array on every render
useEffect(() => {
  // hook returns new array reference each time
}, [extractedScopes])

// CORRECT - hook uses useMemo internally
const { scopeValueIds } = useSystemSectionScopes(...)
useEffect(() => {
  // scopeValueIds is memoized, no infinite loop
}, [scopeValueIds])
```

---

## Permission System Flow

```
User Login
    ↓
PermissionsContext fetches full permission tree from API
    ↓
Page calls useSystemSectionScopes(sectionId)
    ↓
Hook extracts scopeValueIds from specific section
    ↓
Page checks hasPermission() for specific actions
    ↓
Page renders DataTable:
  ├─ Filters rows by scopeValueIds
  ├─ Shows/hides CREATE button
  ├─ Shows/hides UPDATE/DELETE buttons per row
    ↓
User clicks action (Create/Update/Delete)
    ↓
Page sends API request with:
  ├─ Action data (what to create/update/delete)
  ├─ scopeValueIds filter (authorization proof)
    ↓
Backend validates:
  ├─ User has permission for action
  ├─ Data belongs to authorized scopes
    ↓
Response: Success or Access Denied
```

---

## Testing Permission Scenarios

### Scenario 1: User with Scoped Access
- User has APPOINTMENT_SCHEDULING permission
- Scopes: ["Alepp SARC", "SARC Zahehra"]
- **Expected**: Can create/edit/delete only in those 2 branches

### Scenario 2: User with Global Access
- User has Appointment_Reference_Data permission
- Scopes: [] (empty)
- **Expected**: Can create/edit/delete ALL service types, action types, statuses

### Scenario 3: User with No Access
- User has no Appointment_SCHEDULING permission
- **Expected**: "Access Denied" or section hidden in home page

### Scenario 4: User with CREATE but not DELETE
- CREATE action: effect="ALLOW"
- DELETE action: effect="DENY" or missing
- **Expected**: Shows create button, hides delete button

---

## Summary

**Permission control on pages uses a 3-tier approach:**

1. **Section Level**: Does user have access to this section?
   - Use: `hasAccess` from `useSystemSectionScopes`

2. **Action Level**: Does user have permission for this action (Create/Read/Update/Delete)?
   - Use: `hasPermission(actionCode, sectionName)` from `usePermissionCheck`

3. **Scope Level**: Which branches/centers can the user access?
   - Use: `scopeValueIds` from `useSystemSectionScopes`
   - Include in every API call for proper authorization filtering

This ensures both UI controls and backend data filtering work together to enforce permissions correctly.
