-- V1__create_tables.sql
-- Initial database schema

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    user_id BIGINT NOT NULL UNIQUE,

    specialty VARCHAR(30) NOT NULL,
    qualification VARCHAR(200) NOT NULL,
    experience_years INT NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,

    bio TEXT,
    profile_image_url VARCHAR(512),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_doctor_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    user_id BIGINT NOT NULL UNIQUE,

    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL,

    address VARCHAR(500),
    blood_group VARCHAR(5),
    emergency_contact VARCHAR(20),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_patient_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE doctor_availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    doctor_id BIGINT NOT NULL,

    day_of_week VARCHAR(9) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_availability_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id),

    CONSTRAINT uk_availability_doctor_day
        UNIQUE (doctor_id, day_of_week)
);

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,

    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,

    status VARCHAR(15) NOT NULL,

    reason TEXT NOT NULL,
    notes TEXT,
    cancellation_reason VARCHAR(500),

    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id),

    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id),

    CONSTRAINT uk_appointments_doctor_date_time
        UNIQUE (doctor_id, appointment_date, appointment_time)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    user_id BIGINT NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME(6) NOT NULL,

    CONSTRAINT fk_token_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_patients_user_id
ON patients(user_id);

CREATE INDEX idx_appointments_patient_id
ON appointments(patient_id);