-- =====================================================
-- APPOINTMENT SERVICE SEED DATA
-- تاريخ: 2 نوفمبر 2025
-- =====================================================

-- =====================================================
-- 1. SERVICE TYPES (أنواع الخدمات)
-- =====================================================
INSERT INTO service_types (
  id,
  name,
  code,
  description,
  is_active,
  is_deleted,
  created_at,
  created_by,
  updated_at,
  updated_by
) VALUES

-- الفحص العام
(
  '550e8400-e29b-41d4-a716-446655440001'::uuid,
  'فحص عام / General Checkup',
  'GEN_CHECKUP',
  'فحص طبي عام شامل',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- فحص الأطفال
(
  '550e8400-e29b-41d4-a716-446655440002'::uuid,
  'فحص الأطفال / Pediatrics',
  'PEDIATRICS',
  'فحص متخصص للأطفال',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- طب الأسنان
(
  '550e8400-e29b-41d4-a716-446655440003'::uuid,
  'طب الأسنان / Dentistry',
  'DENTISTRY',
  'خدمات طب الأسنان الشاملة',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- طب العيون
(
  '550e8400-e29b-41d4-a716-446655440004'::uuid,
  'طب العيون / Ophthalmology',
  'OPHTHALMOLOGY',
  'فحص وعلاج العيون',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- أمراض القلب
(
  '550e8400-e29b-41d4-a716-446655440005'::uuid,
  'أمراض القلب / Cardiology',
  'CARDIOLOGY',
  'فحص وعلاج أمراض القلب',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
);

-- =====================================================
-- 2. PROVIDERS/DOCTORS (الأطباء والمقدمو الخدمات)
-- =====================================================
INSERT INTO providers (
  provider_id,
  name,
  specialization,
  license_number,
  phone,
  email,
  is_active,
  is_deleted,
  created_at,
  created_by,
  updated_at,
  updated_by
) VALUES

-- د. أحمد محمود
(
  '650e8400-e29b-41d4-a716-446655440001'::uuid,
  'د. أحمد محمود',
  'طبيب عام',
  'LIC-001-2023',
  '07701234567',
  'ahmad.mahmoud@health.iq',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- د. فاطمة علي
(
  '650e8400-e29b-41d4-a716-446655440002'::uuid,
  'د. فاطمة علي',
  'طبيبة أطفال',
  'LIC-002-2023',
  '07702345678',
  'fatima.ali@health.iq',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- د. محمد العبيدي
(
  '650e8400-e29b-41d4-a716-446655440003'::uuid,
  'د. محمد العبيدي',
  'طبيب أسنان',
  'LIC-003-2023',
  '07703456789',
  'mohammad.alubidi@health.iq',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- د. ليلى حسين
(
  '650e8400-e29b-41d4-a716-446655440004'::uuid,
  'د. ليلى حسين',
  'طبيبة عيون',
  'LIC-004-2023',
  '07704567890',
  'layla.hussein@health.iq',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- د. عمر الكرعاوي
(
  '650e8400-e29b-41d4-a716-446655440005'::uuid,
  'د. عمر الكرعاوي',
  'طبيب قلب',
  'LIC-005-2023',
  '07705678901',
  'omar.karawi@health.iq',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
);

-- =====================================================
-- 3. HEALTH CENTERS (المراكز الصحية)
-- =====================================================
INSERT INTO health_centers (
  center_id,
  name,
  name_ar,
  name_en,
  phone,
  email,
  address,
  latitude,
  longitude,
  is_active,
  is_deleted,
  created_at,
  created_by,
  updated_at,
  updated_by
) VALUES

-- مركز الرعاية الأولى
(
  '750e8400-e29b-41d4-a716-446655440001'::uuid,
  'مركز الرعاية الأولية',
  'مركز الرعاية الأولية',
  'Primary Healthcare Center',
  '07712345678',
  'primary@health.iq',
  'شارع الرشيد، بغداد',
  33.3128,
  44.3615,
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- مركز صحة الأسرة
(
  '750e8400-e29b-41d4-a716-446655440002'::uuid,
  'مركز صحة الأسرة',
  'مركز صحة الأسرة',
  'Family Health Center',
  '07723456789',
  'family@health.iq',
  'شارع فلسطين، بغداد',
  33.3215,
  44.3690,
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- مركز الرعاية المتقدمة
(
  '750e8400-e29b-41d4-a716-446655440003'::uuid,
  'مركز الرعاية الصحية المتقدمة',
  'مركز الرعاية الصحية المتقدمة',
  'Advanced Healthcare Center',
  '07734567890',
  'advanced@health.iq',
  'منطقة الكرادة، بغداد',
  33.2947,
  44.3857,
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
);

-- =====================================================
-- 4. BENEFICIARIES (المستفيدون - بيانات الاختبار)
-- =====================================================
INSERT INTO beneficiaries (
  beneficiary_id,
  first_name,
  last_name,
  mobile_number,
  date_of_birth,
  gender,
  email,
  national_id,
  is_active,
  is_deleted,
  created_at,
  created_by,
  updated_at,
  updated_by
) VALUES

-- مستفيد 1: أحمد علي
(
  '850e8400-e29b-41d4-a716-446655440001'::uuid,
  'أحمد',
  'علي',
  '07701234567',
  '1985-05-15'::date,
  'M',
  'ahmad.ali@email.iq',
  '000000001',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- مستفيد 2: فاطمة محمود
(
  '850e8400-e29b-41d4-a716-446655440002'::uuid,
  'فاطمة',
  'محمود',
  '07702345678',
  '1990-03-22'::date,
  'F',
  'fatima.mahmoud@email.iq',
  '000000002',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- مستفيد 3: محمد حسن
(
  '850e8400-e29b-41d4-a716-446655440003'::uuid,
  'محمد',
  'حسن',
  '07703456789',
  '1978-12-08'::date,
  'M',
  'mohammad.hassan@email.iq',
  '000000003',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
);

-- =====================================================
-- 5. TEST APPOINTMENTS (مواعيد الاختبار)
-- =====================================================
INSERT INTO appointments (
  appointment_id,
  beneficiary_id,
  service_type_id,
  provider_id,
  center_id,
  appointment_date,
  appointment_time,
  status,
  notes,
  is_active,
  is_deleted,
  created_at,
  created_by,
  updated_at,
  updated_by
) VALUES

-- موعد 1: أحمد - فحص عام - غداً
(
  '950e8400-e29b-41d4-a716-446655440001'::uuid,
  '850e8400-e29b-41d4-a716-446655440001'::uuid,
  '550e8400-e29b-41d4-a716-446655440001'::uuid,
  '650e8400-e29b-41d4-a716-446655440001'::uuid,
  '750e8400-e29b-41d4-a716-446655440001'::uuid,
  (CURRENT_DATE + INTERVAL '1 day')::date,
  '10:00:00'::time,
  'SCHEDULED',
  'موعد فحص دوري',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- موعد 2: فاطمة - فحص أطفال - بعد غد
(
  '950e8400-e29b-41d4-a716-446655440002'::uuid,
  '850e8400-e29b-41d4-a716-446655440002'::uuid,
  '550e8400-e29b-41d4-a716-446655440002'::uuid,
  '650e8400-e29b-41d4-a716-446655440002'::uuid,
  '750e8400-e29b-41d4-a716-446655440002'::uuid,
  (CURRENT_DATE + INTERVAL '2 days')::date,
  '14:30:00'::time,
  'SCHEDULED',
  'فحص شامل للطفل',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
),

-- موعد 3: محمد - طب أسنان - بعد 3 أيام
(
  '950e8400-e29b-41d4-a716-446655440003'::uuid,
  '850e8400-e29b-41d4-a716-446655440003'::uuid,
  '550e8400-e29b-41d4-a716-446655440003'::uuid,
  '650e8400-e29b-41d4-a716-446655440003'::uuid,
  '750e8400-e29b-41d4-a716-446655440003'::uuid,
  (CURRENT_DATE + INTERVAL '3 days')::date,
  '09:15:00'::time,
  'SCHEDULED',
  'تنظيف الأسنان والفحص',
  true,
  false,
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  'system'
);

-- =====================================================
-- 6. PROVIDER SPECIALIZATIONS (تخصصات الأطباء)
-- =====================================================
INSERT INTO provider_specializations (
  provider_id,
  service_type_id
) VALUES
-- د. أحمد - طبيب عام
('650e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid),

-- د. فاطمة - طبيبة أطفال
('650e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid),

-- د. محمد - طبيب أسنان
('650e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid),

-- د. ليلى - طبيبة عيون
('650e8400-e29b-41d4-a716-446655440004'::uuid, '550e8400-e29b-41d4-a716-446655440004'::uuid),

-- د. عمر - طبيب قلب
('650e8400-e29b-41d4-a716-446655440005'::uuid, '550e8400-e29b-41d4-a716-446655440005'::uuid);

-- =====================================================
-- 7. CENTER SERVICES (الخدمات في المراكز)
-- =====================================================
INSERT INTO center_services (
  center_id,
  service_type_id
) VALUES
-- مركز الرعاية الأولية - جميع الخدمات
('750e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid),
('750e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid),
('750e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440005'::uuid),

-- مركز صحة الأسرة
('750e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid),
('750e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid),

-- مركز الرعاية المتقدمة - جميع التخصصات
('750e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid),
('750e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid),
('750e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440004'::uuid),
('750e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440005'::uuid);

-- =====================================================
-- COMMIT
-- =====================================================
COMMIT;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- 1. تحقق من عدد أنواع الخدمات
-- Expected: 5
SELECT COUNT(*) as "Service Types Count" FROM appt_service_types WHERE is_deleted = false;

-- 2. تحقق من عدد الأطباء
-- Expected: 5
SELECT COUNT(*) as "Providers Count" FROM appt_providers WHERE is_deleted = false;

-- 3. تحقق من عدد المراكز
-- Expected: 3
SELECT COUNT(*) as "Health Centers Count" FROM appt_health_centers WHERE is_deleted = false;

-- 4. تحقق من عدد المستفيدين
-- Expected: 3
SELECT COUNT(*) as "Beneficiaries Count" FROM appt_beneficiaries WHERE is_deleted = false;

-- 5. تحقق من عدد المواعيد
-- Expected: 3
SELECT COUNT(*) as "Appointments Count" FROM appt_appointments WHERE is_deleted = false;

-- 6. قائمة أنواع الخدمات (للموبايل)
SELECT
  service_type_id,
  name,
  is_active
FROM appt_service_types
WHERE is_deleted = false AND is_active = true
ORDER BY name;

-- 7. بيانات المستفيدين للاختبار
SELECT
  beneficiary_id,
  first_name,
  last_name,
  mobile_number,
  date_of_birth,
  email
FROM appt_beneficiaries
WHERE is_deleted = false
ORDER BY created_at DESC;

-- =====================================================
-- APPOINTMENT SERVICE SEED DATA - FIXED
-- تاريخ: 2 نوفمبر 2025
-- Uses correct table names from JPA entities
-- =====================================================

-- =====================================================
-- 1. SERVICE TYPES (أنواع الخدمات)
-- =====================================================
INSERT INTO service_types (
  service_type_id,
  name,
  code,
  description,
  parent_service_type_id,
  is_active,
  is_deleted,
  is_leaf,
  display_order,
  created_by_user_id,
  created_at,
  updated_by_user_id,
  updated_at,
  row_version
) VALUES

-- الفحص العام
(
  '550e8400-e29b-41d4-a716-446655440001'::uuid,
  'فحص عام / General Checkup',
  'GEN_CHECKUP',
  'فحص طبي عام شامل',
  NULL,
  true,
  false,
  true,
  1,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- فحص الأطفال
(
  '550e8400-e29b-41d4-a716-446655440002'::uuid,
  'فحص الأطفال / Pediatrics',
  'PEDIATRICS',
  'فحص متخصص للأطفال',
  NULL,
  true,
  false,
  true,
  2,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- طب الأسنان
(
  '550e8400-e29b-41d4-a716-446655440003'::uuid,
  'طب الأسنان / Dentistry',
  'DENTISTRY',
  'خدمات طب الأسنان الشاملة',
  NULL,
  true,
  false,
  true,
  3,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- طب العيون
(
  '550e8400-e29b-41d4-a716-446655440004'::uuid,
  'طب العيون / Ophthalmology',
  'OPHTHALMOLOGY',
  'فحص وعلاج العيون',
  NULL,
  true,
  false,
  true,
  4,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- أمراض القلب
(
  '550e8400-e29b-41d4-a716-446655440005'::uuid,
  'أمراض القلب / Cardiology',
  'CARDIOLOGY',
  'فحص وعلاج أمراض القلب',
  NULL,
  true,
  false,
  true,
  5,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
)
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- 2. BENEFICIARIES (المستفيدون - بيانات الاختبار)
-- =====================================================
INSERT INTO beneficiaries (
  beneficiary_id,
  national_id,
  full_name,
  mother_name,
  mobile_number,
  email,
  address,
  latitude,
  longitude,
  date_of_birth,
  gender_code_value_id,
  profile_photo_url,
  registration_status_code_value_id,
  registration_completed_at,
  registration_completed_by_user_id,
  preferred_language_code_value_id,
  is_active,
  is_deleted,
  created_by_user_id,
  created_at,
  updated_by_user_id,
  updated_at,
  row_version
) VALUES

-- مستفيد 1: أحمد علي
(
  '850e8400-e29b-41d4-a716-446655440001'::uuid,
  '000000001',
  'أحمد علي',
  NULL,
  '+9647701234567',
  'ahmad.ali@email.iq',
  NULL,
  NULL,
  NULL,
  '1985-05-15'::date,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  true,
  false,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- مستفيد 2: فاطمة محمود
(
  '850e8400-e29b-41d4-a716-446655440002'::uuid,
  '000000002',
  'فاطمة محمود',
  NULL,
  '+9647702345678',
  'fatima.mahmoud@email.iq',
  NULL,
  NULL,
  NULL,
  '1990-03-22'::date,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  true,
  false,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
),

-- مستفيد 3: محمد حسن
(
  '850e8400-e29b-41d4-a716-446655440003'::uuid,
  '000000003',
  'محمد حسن',
  NULL,
  '+9647703456789',
  'mohammad.hassan@email.iq',
  NULL,
  NULL,
  NULL,
  '1978-12-08'::date,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  true,
  false,
  NULL,
  CURRENT_TIMESTAMP,
  NULL,
  CURRENT_TIMESTAMP,
  0
)
ON CONFLICT (national_id) DO NOTHING;

-- =====================================================
-- COMMIT
-- =====================================================

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- 1. تحقق من عدد أنواع الخدمات
-- Expected: 5
SELECT COUNT(*) as "Service Types Count" FROM service_types WHERE is_deleted = false;

-- 2. تحقق من عدد المستفيدين
-- Expected: 3
SELECT COUNT(*) as "Beneficiaries Count" FROM beneficiaries WHERE is_deleted = false;

-- 3. قائمة أنواع الخدمات (للموبايل)
SELECT
  service_type_id,
  name,
  is_active
FROM service_types
WHERE is_deleted = false AND is_active = true
ORDER BY display_order, name;

-- 4. بيانات المستفيدين للاختبار
SELECT
  beneficiary_id,
  full_name,
  mobile_number,
  date_of_birth,
  email
FROM beneficiaries
WHERE is_deleted = false
ORDER BY created_at DESC;
