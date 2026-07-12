ALTER TABLE users
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE doctors
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE patients
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE doctor_availabilities
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE appointments
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE refresh_tokens
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);