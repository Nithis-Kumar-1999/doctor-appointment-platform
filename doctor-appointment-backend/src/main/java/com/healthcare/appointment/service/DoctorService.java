package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.request.DoctorRequest;
import com.healthcare.appointment.dto.response.DoctorResponse;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.enums.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

    DoctorResponse createProfile(Long userId, DoctorRequest request);

    DoctorResponse updateProfile(Long userId, DoctorRequest request);

    DoctorResponse getProfileByUserId(Long userId);

    Doctor getDoctorEntityById(Long doctorId);

    Page<DoctorResponse> searchDoctors(Specialty specialty, String city, Pageable pageable);
}
