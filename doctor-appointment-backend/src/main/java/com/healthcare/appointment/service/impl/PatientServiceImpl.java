package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.dto.request.PatientRequest;
import com.healthcare.appointment.dto.response.PatientResponse;
import com.healthcare.appointment.entity.Patient;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.PatientRepository;
import com.healthcare.appointment.service.PatientService;
import com.healthcare.appointment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;

    @Override
    @Transactional
    public PatientResponse createProfile(Long userId, PatientRequest request) {
        if (patientRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("Patient profile already exists for this user");
        }

        User user = userService.getUserById(userId);
        if (user.getRole() != Role.PATIENT) {
            throw new InvalidOperationException("User role must be PATIENT to create a patient profile");
        }

        Patient patient = Patient.builder()
                .user(user)
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .phone(request.phone())
                .address(request.address())
                .bloodGroup(request.bloodGroup())
                .emergencyContact(request.emergencyContact())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return mapToResponse(savedPatient);
    }

    @Override
    @Transactional
    public PatientResponse updateProfile(Long userId, PatientRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        patient.setBloodGroup(request.bloodGroup());
        patient.setEmergencyContact(request.emergencyContact());

        Patient updatedPatient = patientRepository.save(patient);
        return mapToResponse(updatedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getProfileByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return mapToResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientEntityByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found. Please create your profile first."));
    }

    private PatientResponse mapToResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getFirstName(),
                patient.getUser().getLastName(),
                patient.getUser().getEmail(),
                patient.getDateOfBirth(),
                patient.getGender().name(),
                patient.getGender().getDisplayName(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getBloodGroup(),
                patient.getEmergencyContact(),
                patient.isActive(),
                patient.getCreatedAt()
        );
    }
}
