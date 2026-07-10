package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.dto.request.DoctorRequest;
import com.healthcare.appointment.dto.response.DoctorResponse;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.enums.Specialty;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.DoctorRepository;
import com.healthcare.appointment.service.DoctorService;
import com.healthcare.appointment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;

    @Override
    @Transactional
    public DoctorResponse createProfile(Long userId, DoctorRequest request) {
        if (doctorRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("Doctor profile already exists for this user");
        }

        User user = userService.getUserById(userId);
        if (user.getRole() != Role.DOCTOR) {
            throw new InvalidOperationException("User role must be DOCTOR to create a doctor profile");
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialty(request.specialty())
                .qualification(request.qualification())
                .experienceYears(request.experienceYears())
                .consultationFee(request.consultationFee())
                .phone(request.phone())
                .city(request.city())
                .bio(request.bio())
                .profileImageUrl(request.profileImageUrl())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return mapToResponse(savedDoctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateProfile(Long userId, DoctorRequest request) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        doctor.setSpecialty(request.specialty());
        doctor.setQualification(request.qualification());
        doctor.setExperienceYears(request.experienceYears());
        doctor.setConsultationFee(request.consultationFee());
        doctor.setPhone(request.phone());
        doctor.setCity(request.city());
        doctor.setBio(request.bio());
        doctor.setProfileImageUrl(request.profileImageUrl());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        return mapToResponse(updatedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getProfileByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor getDoctorEntityById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> searchDoctors(Specialty specialty, String city, Pageable pageable) {
        Page<Doctor> doctors;

        if (specialty != null && city != null) {
            doctors = doctorRepository.findBySpecialtyAndCityIgnoreCaseAndActiveTrue(specialty, city, pageable);
        } else if (specialty != null) {
            doctors = doctorRepository.findBySpecialtyAndActiveTrue(specialty, pageable);
        } else if (city != null) {
            doctors = doctorRepository.findByCityIgnoreCaseAndActiveTrue(city, pageable);
        } else {
            doctors = doctorRepository.findAllByActiveTrue(pageable);
        }

        return doctors.map(this::mapToResponse);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getFirstName(),
                doctor.getUser().getLastName(),
                doctor.getUser().getEmail(),
                doctor.getSpecialty().name(),
                doctor.getSpecialty().getDisplayName(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getConsultationFee(),
                doctor.getPhone(),
                doctor.getCity(),
                doctor.getBio(),
                doctor.getProfileImageUrl(),
                doctor.isActive(),
                doctor.getCreatedAt()
        );
    }
}
