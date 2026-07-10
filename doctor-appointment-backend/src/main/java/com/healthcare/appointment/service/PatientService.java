package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.request.PatientRequest;
import com.healthcare.appointment.dto.response.PatientResponse;
import com.healthcare.appointment.entity.Patient;

public interface PatientService {

    PatientResponse createProfile(Long userId, PatientRequest request);

    PatientResponse updateProfile(Long userId, PatientRequest request);

    PatientResponse getProfileByUserId(Long userId);

    Patient getPatientEntityByUserId(Long userId);
}
