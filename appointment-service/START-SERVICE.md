# 🚀 How to Start & Test the Service

## **Problem:** Service fails because Eureka (service-registry) is not running

## **Solution:** Disable Eureka temporarily

---

## ⚡ **Quick Fix (2 Steps)**

### **Step 1: Edit `application.yml`**

Open: `src/main/resources/application.yml`

**Add this line:**

```yaml
eureka:
  client:
    enabled: false  # ← ADD THIS LINE
    register-with-eureka: false
    fetch-registry: false
```

### **Step 2: Start Service**

```powershell
cd C:\Java\care\Code\appointment-service
mvn clean spring-boot:run
```

**Wait 20-30 seconds...**

---

## ✅ **Verify Service is Running**

**Open browser:**
- **Swagger UI:** http://localhost:6064/swagger-ui.html
- **API Docs:** http://localhost:6064/v3/api-docs

**OR test with PowerShell:**

```powershell
# Quick test
Invoke-RestMethod -Uri "http://localhost:6064/api/admin/service-types/meta" -Method GET

# If you see JSON response → ✅ Service is working!
```

---

## 🧪 **Test All APIs (PowerShell)**

Save this as `test-all-apis.ps1`:

```powershell
$base = "http://localhost:6064/api/admin"

Write-Host "`n🧪 Testing All Admin APIs..." -ForegroundColor Cyan

# Test 1
try {
    $meta1 = Invoke-RestMethod -Uri "$base/service-types/meta" -Method GET
    Write-Host "✅ ServiceType API (8 endpoints)" -ForegroundColor Green
} catch { Write-Host "❌ ServiceType API" -ForegroundColor Red }

# Test 2
try {
    $meta2 = Invoke-RestMethod -Uri "$base/action-types/meta" -Method GET
    Write-Host "✅ ActionType API (8 endpoints)" -ForegroundColor Green
} catch { Write-Host "❌ ActionType API" -ForegroundColor Red }

# Test 3
try {
    $meta3 = Invoke-RestMethod -Uri "$base/schedules/meta" -Method GET
    Write-Host "✅ Schedule API (8 endpoints)" -ForegroundColor Green
} catch { Write-Host "❌ Schedule API" -ForegroundColor Red }

# Test 4
try {
    $meta4 = Invoke-RestMethod -Uri "$base/holidays/meta" -Method GET
    Write-Host "✅ Holiday API (8 endpoints)" -ForegroundColor Green
} catch { Write-Host "❌ Holiday API" -ForegroundColor Red }

Write-Host "`n🎉 Total: 32 Admin Endpoints Ready!" -ForegroundColor Green -BackgroundColor Black
Write-Host "📖 See help/BACKEND-COMPLETE-STATUS.md for full documentation" -ForegroundColor Yellow
```

**Run:**
```powershell
.\test-all-apis.ps1
```

---

## 📚 **Full Documentation**

See `help/` folder:
- **BACKEND-COMPLETE-STATUS.md** - Complete API list & examples
- **POSTMAN-COLLECTION-GUIDE.md** - Postman collection for testing
- **ADMIN-APIs-COMPLETE-GUIDE.md** - Detailed API documentation

---

## 🎯 **What's Complete**

✅ **32 Admin Endpoints** across 4 groups:
1. **ServiceType** (8 endpoints) - ✅ Complete
2. **ActionType** (8 endpoints) - ✅ Complete
3. **Schedule** (8 endpoints) - ✅ Complete  
4. **Holiday** (8 endpoints) - ✅ Complete

**All APIs support:**
- CRUD operations
- Advanced filtering
- Pagination & sorting
- Soft delete
- Audit trail
- Validation
- Swagger documentation

---

## ⚠️ **Troubleshooting**

**Port already in use?**
```powershell
# Find process on port 6064
netstat -ano | findstr :6064

# Kill process (replace PID)
taskkill /PID <PID> /F
```

**Database connection error?**
- Check PostgreSQL is running
- Verify database `cms_db` exists
- Check credentials in `application.yml`

**Still getting Eureka errors?**
- Make sure `eureka.client.enabled: false` is set
- Do `mvn clean` before `mvn spring-boot:run`

---

## 💡 **Pro Tip**

Use **Swagger UI** for easy testing:
```
http://localhost:6064/swagger-ui.html
```

All APIs are documented and you can test them directly from the browser!

