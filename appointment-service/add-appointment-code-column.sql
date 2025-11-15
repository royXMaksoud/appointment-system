-- Add missing columns to appointments table
-- This script adds columns that are defined in AppointmentEntity but missing from the database

-- Add appointment_code column with temporary default value
ALTER TABLE public.appointments
ADD COLUMN IF NOT EXISTS appointment_code VARCHAR(255) DEFAULT 'PENDING-CODE';

-- Add other QR/verification columns
ALTER TABLE public.appointments
ADD COLUMN IF NOT EXISTS qr_code_url TEXT;

ALTER TABLE public.appointments
ADD COLUMN IF NOT EXISTS verification_code VARCHAR(10);

ALTER TABLE public.appointments
ADD COLUMN IF NOT EXISTS verification_code_expires_at TIMESTAMP;

-- Create index for appointment_code if it doesn't exist
CREATE INDEX IF NOT EXISTS ix_appointments_code ON public.appointments(appointment_code);

-- Create index for verification_code if it doesn't exist
CREATE INDEX IF NOT EXISTS ix_appointments_verification_code ON public.appointments(verification_code);

-- Update any existing rows with a proper appointment code
UPDATE public.appointments
SET appointment_code = CONCAT('APPT-', TO_CHAR(appointment_id::text))
WHERE appointment_code = 'PENDING-CODE' OR appointment_code IS NULL;

-- Add UNIQUE constraint on appointment_code if it doesn't exist
ALTER TABLE public.appointments
ADD CONSTRAINT uk_appointment_code UNIQUE (appointment_code);

-- Make appointment_code NOT NULL
ALTER TABLE public.appointments
ALTER COLUMN appointment_code SET NOT NULL;

COMMIT;
