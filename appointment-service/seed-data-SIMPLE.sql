-- =====================================================
-- SIMPLE SEED DATA FOR TESTING
-- =====================================================

-- 1. Service Types
INSERT INTO service_types (id, name, code, description, is_active, is_deleted)
VALUES
('550e8400-e29b-41d4-a716-446655440001'::uuid, 'فحص عام / General Checkup', 'GEN_CHECKUP', 'فحص طبي عام', true, false),
('550e8400-e29b-41d4-a716-446655440002'::uuid, 'فحص الأطفال / Pediatrics', 'PEDIATRICS', 'فحص متخصص للأطفال', true, false),
('550e8400-e29b-41d4-a716-446655440003'::uuid, 'طب الأسنان / Dentistry', 'DENTISTRY', 'خدمات طب الأسنان', true, false),
('550e8400-e29b-41d4-a716-446655440004'::uuid, 'طب العيون / Ophthalmology', 'OPHTHALMOLOGY', 'فحص وعلاج العيون', true, false),
('550e8400-e29b-41d4-a716-446655440005'::uuid, 'أمراض القلب / Cardiology', 'CARDIOLOGY', 'فحص وعلاج القلب', true, false);

-- 2. Beneficiaries
INSERT INTO beneficiaries (id, first_name, last_name, mobile_number, date_of_birth, gender, email, national_id, is_active, is_deleted)
VALUES
('850e8400-e29b-41d4-a716-446655440001'::uuid, 'أحمد', 'علي', '07701234567', '1985-05-15'::date, 'M', 'ahmad@email.iq', '000000001', true, false),
('850e8400-e29b-41d4-a716-446655440002'::uuid, 'فاطمة', 'محمود', '07702345678', '1990-03-22'::date, 'F', 'fatima@email.iq', '000000002', true, false),
('850e8400-e29b-41d4-a716-446655440003'::uuid, 'محمد', 'حسن', '07703456789', '1978-12-08'::date, 'M', 'mohammad@email.iq', '000000003', true, false);

-- VERIFICATION
SELECT COUNT(*) as "Service Types" FROM service_types WHERE is_deleted = false;
SELECT COUNT(*) as "Beneficiaries" FROM beneficiaries WHERE is_deleted = false;
