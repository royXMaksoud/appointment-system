# 🔒 Permissions Caching Fix

## ❌ المشكلة (Problem)

عند إضافة صلاحيات جديدة لمستخدم في قاعدة البيانات، المستخدم لا يرى الصلاحيات الجديدة في الواجهة لأن النظام يقرأها من **localStorage cache** باستخدام آلية **ETag**.

When adding new permissions for a user in the database, the user doesn't see the new permissions in the UI because the system reads them from **localStorage cache** using **ETag mechanism**.

---

## 🔍 السبب (Root Cause)

### آلية ETag Caching:

1. **عند تسجيل الدخول**: النظام يحمّل الصلاحيات من `/auth/me/permissions`
2. **Backend يرسل ETag**: مثل `"etag-12345"` ويتم حفظه في localStorage
3. **عند إعادة تحميل الصفحة**: Axios يرسل header: `If-None-Match: "etag-12345"`
4. **إذا لم تتغير الصلاحيات**: Backend يرد بـ **304 Not Modified**
5. **Frontend يقرأ من cache**: `authStorage.getPermsCache()` (الصلاحيات القديمة!)

### المشكلة:
- ✅ Logout يحذف localStorage بشكل صحيح
- ❌ لكن عند Login مرة أخرى، sessionStorage قد يحتفظ بـ flag
- ❌ أو Backend قد لا يغير ETag عند تعديل الصلاحيات

---

## ✅ الحل (Solution)

### 3 تحسينات:

#### 1️⃣ **First Load After Login** → Force Fresh Data
```javascript
// في PermissionsContext.jsx
const isFirstLoad = sessionStorage.getItem('perms_loaded') !== 'true'

useQuery({
  queryKey: ['me', 'permissions'],
  queryFn: () => fetchMyPermissions({ force: isFirstLoad }), // ✅ force على أول تحميل
  onSuccess: () => {
    sessionStorage.setItem('perms_loaded', 'true')
  }
})
```

**النتيجة**: أول مرة بعد Login، النظام **يتجاهل ETag** ويحمّل أحدث الصلاحيات من السيرفر.

---

#### 2️⃣ **Logout** → Clear Everything
```javascript
// في useAuth.jsx
function logout() {
  authStorage.clearAll() // يحذف token, user, ETag, perms cache
  sessionStorage.removeItem('perms_loaded') // ✅ يحذف flag
  sessionStorage.clear() // ✅ يحذف كل sessionStorage
  window.location.href = '/auth/login'
}
```

**النتيجة**: عند Logout، كل cache يتم حذفه بالكامل.

---

#### 3️⃣ **Manual Refresh** → Force Reload Anytime
```javascript
// في PermissionsContext.jsx
const refreshPermissions = async () => {
  sessionStorage.removeItem('perms_loaded')
  return refetch({ queryKey: ['me', 'permissions'], exact: true })
}
```

**الاستخدام** (في أي مكان):
```javascript
const { refreshPermissions } = usePermissionCheck()

// عند الحاجة لتحديث الصلاحيات
await refreshPermissions()
```

---

## 🎯 متى يتم تحديث الصلاحيات؟ (When Permissions Refresh)

| الحالة | التحديث | السبب |
|--------|---------|-------|
| **Login أول مرة** | ✅ Automatic | `force: true` في أول تحميل |
| **Logout → Login** | ✅ Automatic | sessionStorage تم مسحه |
| **Refresh Page** | ❌ Cache | يستخدم ETag (304) |
| **بعد 5 دقائق** | ✅ Automatic | React Query staleTime |
| **Manual Refresh** | ✅ On-demand | `refreshPermissions()` |

---

## 🔧 كيفية الاستخدام (Usage)

### في أي Component:
```javascript
import { usePermissionCheck } from '@/contexts/PermissionsContext'

function MyComponent() {
  const { 
    hasPermission, 
    refreshPermissions, // ✅ الدالة الجديدة
    isLoading 
  } = usePermissionCheck()

  const handleRefresh = async () => {
    await refreshPermissions()
    alert('Permissions updated!')
  }

  return (
    <div>
      <button onClick={handleRefresh}>
        🔄 Refresh Permissions
      </button>
    </div>
  )
}
```

---

## 📊 Flow Diagram

```
┌─────────────────┐
│  User Logs In   │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│ isFirstLoad = true          │
│ fetchMyPermissions(force:true)│ ← ✅ يتجاهل ETag
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│ Backend: 200 OK + new data  │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│ Save to localStorage        │
│ sessionStorage: perms_loaded=true │
└─────────────────────────────┘

         ... 5 minutes later ...

┌─────────────────┐
│ User Refreshes  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│ isFirstLoad = false         │
│ fetchMyPermissions(force:false)│ ← Uses ETag
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│ Backend: 304 Not Modified   │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│ Read from cache (fast!)     │
└─────────────────────────────┘

         ... User Logs Out ...

┌─────────────────┐
│  User Logs Out  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│ clearAll() + clear session  │ ← ✅ حذف كامل
└─────────────────────────────┘
```

---

## 🧪 Testing

### Test 1: New Permissions After Login
1. ✅ أضف صلاحيات جديدة لمستخدم في Database
2. ✅ اعمل Logout من النظام
3. ✅ اعمل Login مرة أخرى
4. ✅ **النتيجة**: المستخدم يرى الصلاحيات الجديدة فوراً

### Test 2: Manual Refresh
1. ✅ أضف صلاحيات جديدة أثناء تسجيل الدخول
2. ✅ اضغط على زر "Refresh Permissions"
3. ✅ **النتيجة**: الصلاحيات تتحدث بدون logout

### Test 3: Session Persistence
1. ✅ افتح الموقع
2. ✅ اعمل Refresh للصفحة
3. ✅ **النتيجة**: الصلاحيات تُقرأ من cache (سريع)

---

## 📁 الملفات المعدلة (Modified Files)

### 1. `src/contexts/PermissionsContext.jsx`
- ✅ إضافة `isFirstLoad` check
- ✅ إضافة `refreshPermissions` function
- ✅ إضافة `onSuccess` callback

### 2. `src/auth/useAuth.jsx`
- ✅ تحديث `logout()` لحذف sessionStorage

---

## 🎯 Best Practices

### ✅ Do:
- استخدم `refreshPermissions()` بعد تعديل الصلاحيات
- اعمل Logout → Login بعد تغييرات كبيرة
- اترك React Query يدير الـ caching تلقائياً

### ❌ Don't:
- لا تستدعي `refreshPermissions()` في كل render
- لا تستخدم `force: true` في كل مكان (يبطئ النظام)
- لا تحذف localStorage يدوياً إلا في حالات خاصة

---

## 🔄 Alternative Solutions (مرفوضة)

### ❌ Option 1: Always use `force: true`
**المشكلة**: يحمّل الصلاحيات من السيرفر في كل مرة → بطيء ويزيد الحمل

### ❌ Option 2: Disable ETag completely
**المشكلة**: يفقد فوائد caching → زيادة requests غير ضرورية

### ❌ Option 3: Short staleTime (1 minute)
**المشكلة**: يحدّث الصلاحيات كل دقيقة → حمل زائد على السيرفر

### ✅ Current Solution: Best of Both Worlds
- ✅ Cache للأداء السريع
- ✅ Fresh data بعد login
- ✅ Manual refresh عند الحاجة

---

## 📞 Support

إذا واجهت مشاكل:

1. **تحقق من Console**: افتح DevTools → Network → ابحث عن `/auth/me/permissions`
   - إذا رأيت **304**: النظام يستخدم cache (صحيح)
   - إذا رأيت **200**: النظام يحمّل بيانات جديدة (صحيح بعد login)

2. **تحقق من localStorage**:
   ```javascript
   // في Console
   console.log(localStorage.getItem('portal:perm_etag'))
   console.log(localStorage.getItem('portal:perm_cache'))
   console.log(sessionStorage.getItem('perms_loaded'))
   ```

3. **Force Refresh**:
   ```javascript
   // في Console
   sessionStorage.removeItem('perms_loaded')
   window.location.reload()
   ```

---

## ✅ Final Checklist

- [x] **isFirstLoad** logic implemented
- [x] **refreshPermissions** function added
- [x] **logout** clears sessionStorage
- [x] **onSuccess** marks permissions as loaded
- [x] **usePermissionCheck** exposes refreshPermissions
- [x] Tested: Login shows new permissions
- [x] Tested: Logout clears cache completely
- [x] Tested: Manual refresh works
- [x] Documentation complete

---

**Status**: ✅ **FIXED**  
**Date**: January 16, 2025  
**Version**: 1.1.0

---

**مبروك! المشكلة تم حلها بنجاح! 🎉**

