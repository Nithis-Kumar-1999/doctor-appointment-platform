package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.enums.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Doctor} entity and {@link Specialty} enum.
 *
 * <p><b>What is tested:</b>
 * <ul>
 *   <li>Builder pattern sets all fields correctly.</li>
 *   <li>{@code @Builder.Default} sets {@code active = true} without explicit call.</li>
 *   <li>{@code toString()} does NOT trigger lazy loading of the {@code user} field
 *       (verified by confirming user details do NOT appear in output).</li>
 *   <li>{@code equals()} and {@code hashCode()} follow the id-based strategy.</li>
 *   <li>{@link Specialty} enum has correct display names for all constants.</li>
 * </ul>
 *
 * @author Nithish Kumar
 */
@DisplayName("Doctor Entity and Specialty Enum Unit Tests")
class DoctorTest {

    // =========================================================================
    // HELPER
    // =========================================================================

    private User buildUser() {
        return User.builder()
                .firstName("Ravi")
                .lastName("Shankar")
                .email("ravi.shankar@healthcare.com")
                .password("$2a$12$hashedPassword")
                .role(Role.DOCTOR)
                .build();
    }

    private Doctor buildDoctor() {
        return Doctor.builder()
                .user(buildUser())
                .specialty(Specialty.CARDIOLOGY)
                .qualification("MBBS, MD (Cardiology)")
                .experienceYears(10)
                .consultationFee(new BigDecimal("800.00"))
                .phone("+91-9876543210")
                .city("Chennai")
                .bio("Senior Cardiologist with 10+ years of experience.")
                .profileImageUrl("https://cdn.healthcare.com/doctors/ravi.jpg")
                .build();
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderTests {

        @Test
        @DisplayName("Builder should correctly set all professional fields")
        void builder_withAllFields_shouldSetCorrectValues() {
            Doctor doctor = buildDoctor();

            assertThat(doctor.getSpecialty()).isEqualTo(Specialty.CARDIOLOGY);
            assertThat(doctor.getQualification()).isEqualTo("MBBS, MD (Cardiology)");
            assertThat(doctor.getExperienceYears()).isEqualTo(10);
            assertThat(doctor.getConsultationFee()).isEqualByComparingTo(new BigDecimal("800.00"));
            assertThat(doctor.getPhone()).isEqualTo("+91-9876543210");
            assertThat(doctor.getCity()).isEqualTo("Chennai");
            assertThat(doctor.getBio()).isEqualTo("Senior Cardiologist with 10+ years of experience.");
            assertThat(doctor.getProfileImageUrl()).isEqualTo("https://cdn.healthcare.com/doctors/ravi.jpg");
        }

        @Test
        @DisplayName("Builder should correctly set the User relationship")
        void builder_shouldSetUserRelationship() {
            Doctor doctor = buildDoctor();

            assertThat(doctor.getUser()).isNotNull();
            assertThat(doctor.getUser().getEmail()).isEqualTo("ravi.shankar@healthcare.com");
            assertThat(doctor.getUser().getRole()).isEqualTo(Role.DOCTOR);
        }

        @Test
        @DisplayName("@Builder.Default should set active=true when not explicitly provided")
        void builder_withoutActiveField_shouldDefaultToTrue() {
            // GIVEN: Builder called WITHOUT setting active field
            Doctor doctor = Doctor.builder()
                    .user(buildUser())
                    .specialty(Specialty.NEUROLOGY)
                    .qualification("MBBS, MD")
                    .experienceYears(5)
                    .consultationFee(new BigDecimal("500.00"))
                    .phone("9876543210")
                    .city("Bangalore")
                    .build();

            // THEN: Must be true — proves @Builder.Default is working
            assertThat(doctor.isActive())
                    .as("Newly created Doctor should be active by default")
                    .isTrue();
        }

        @Test
        @DisplayName("Builder should allow explicitly setting active=false (deactivation)")
        void builder_withActiveFalse_shouldBeDeactivated() {
            Doctor doctor = Doctor.builder()
                    .user(buildUser())
                    .specialty(Specialty.DERMATOLOGY)
                    .qualification("MBBS, MD")
                    .experienceYears(3)
                    .consultationFee(new BigDecimal("300.00"))
                    .phone("9876543210")
                    .city("Mumbai")
                    .active(false)
                    .build();

            assertThat(doctor.isActive()).isFalse();
        }

        @Test
        @DisplayName("Optional fields (bio, profileImageUrl) should be null when not set")
        void builder_withoutOptionalFields_shouldHaveNullOptionalFields() {
            Doctor doctor = Doctor.builder()
                    .user(buildUser())
                    .specialty(Specialty.GENERAL_MEDICINE)
                    .qualification("MBBS")
                    .experienceYears(2)
                    .consultationFee(new BigDecimal("200.00"))
                    .phone("9876543210")
                    .city("Delhi")
                    .build();

            assertThat(doctor.getBio()).isNull();
            assertThat(doctor.getProfileImageUrl()).isNull();
        }
    }

    // =========================================================================
    // CONSULTATION FEE (BigDecimal) TESTS
    // =========================================================================

    @Nested
    @DisplayName("Consultation Fee — BigDecimal Precision")
    class ConsultationFeeTests {

        @Test
        @DisplayName("isEqualByComparingTo should be used for BigDecimal equality (not equals)")
        void consultationFee_shouldUseBigDecimalComparison() {
            // IMPORTANT LESSON: BigDecimal("800.00").equals(BigDecimal("800.0")) is FALSE
            // because equals() considers scale. isEqualByComparingTo() ignores scale.
            Doctor doctor = buildDoctor();

            // CORRECT: compareTo ignores scale
            assertThat(doctor.getConsultationFee())
                    .isEqualByComparingTo(new BigDecimal("800"));

            // WRONG APPROACH (would FAIL — DO NOT use .isEqualTo() for BigDecimal money values):
            // assertThat(doctor.getConsultationFee()).isEqualTo(new BigDecimal("800"));
        }

        @Test
        @DisplayName("Consultation fee should support decimal precision correctly")
        void consultationFee_shouldHandleDecimalPrecisionCorrectly() {
            Doctor doctor = Doctor.builder()
                    .user(buildUser())
                    .specialty(Specialty.PSYCHIATRY)
                    .qualification("MBBS, MD")
                    .experienceYears(8)
                    .consultationFee(new BigDecimal("1250.50"))
                    .phone("9876543210")
                    .city("Hyderabad")
                    .build();

            // BigDecimal preserves exact decimal — no floating-point rounding error
            assertThat(doctor.getConsultationFee())
                    .isEqualByComparingTo(new BigDecimal("1250.50"));
        }
    }

    // =========================================================================
    // EQUALS AND HASHCODE TESTS
    // =========================================================================

    @Nested
    @DisplayName("equals() and hashCode() — Id-Based Strategy")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Two unpersisted doctors (null id) should NOT be equal to each other")
        void equals_givenBothIdsNull_shouldNotBeEqual() {
            // Two brand-new, unsaved doctors — neither has an id yet
            Doctor doctor1 = buildDoctor();
            Doctor doctor2 = buildDoctor();

            // Different object references, null ids → not equal
            // This is correct: we cannot determine equality without the DB id
            assertThat(doctor1).isNotEqualTo(doctor2);
        }

        @Test
        @DisplayName("Doctor should be equal to itself (reflexive)")
        void equals_withSelf_shouldBeTrue() {
            Doctor doctor = buildDoctor();
            assertThat(doctor).isEqualTo(doctor);
        }

        @Test
        @DisplayName("Doctor should NOT equal null")
        void equals_withNull_shouldBeFalse() {
            Doctor doctor = buildDoctor();
            assertThat(doctor).isNotEqualTo(null);
        }

        @Test
        @DisplayName("hashCode should be consistent across multiple invocations")
        void hashCode_shouldBeConsistentAcrossMultipleCalls() {
            Doctor doctor = buildDoctor();
            assertThat(doctor.hashCode()).isEqualTo(doctor.hashCode());
        }

        @Test
        @DisplayName("hashCode constant strategy: all Doctor instances share the same hashCode")
        void hashCode_constantStrategy_allInstancesShareSameHashCode() {
            // This is expected with getClass().hashCode() strategy.
            // The trade-off (all in same hash bucket) is acceptable because
            // large in-memory Doctor sets are rare — they're paginated from DB.
            Doctor doctor1 = buildDoctor();
            Doctor doctor2 = buildDoctor();

            assertThat(doctor1.hashCode()).isEqualTo(doctor2.hashCode());
        }
    }

    // =========================================================================
    // TOSTRING LAZY-LOADING SAFETY TEST
    // =========================================================================

    @Nested
    @DisplayName("toString() — Lazy Loading Safety")
    class ToStringTests {

        @Test
        @DisplayName("toString() should NOT include user field (prevents LazyInitializationException)")
        void toString_shouldNotContainUserDetails() {
            Doctor doctor = buildDoctor();
            String result = doctor.toString();

            // user field is excluded via @ToString(exclude = "user")
            // If "user" appeared, calling toString() on a detached Doctor entity
            // would trigger Hibernate lazy loading and throw LazyInitializationException.
            assertThat(result).doesNotContain("ravi.shankar@healthcare.com");
            assertThat(result).doesNotContain("hashedPassword");
        }

        @Test
        @DisplayName("toString() should include non-sensitive professional fields")
        void toString_shouldContainProfessionalFields() {
            Doctor doctor = buildDoctor();
            String result = doctor.toString();

            // Safe fields should appear for debugging purposes
            assertThat(result).contains("CARDIOLOGY");
            assertThat(result).contains("Chennai");
        }
    }

    // =========================================================================
    // SPECIALTY ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("Specialty Enum")
    class SpecialtyEnumTests {

        @Test
        @DisplayName("Specialty enum should contain exactly 20 values")
        void specialty_shouldContainExactly20Values() {
            assertThat(Specialty.values()).hasSize(20);
        }

        @Test
        @DisplayName("Each Specialty should have a non-blank displayName")
        void specialty_allValues_shouldHaveNonBlankDisplayName() {
            for (Specialty specialty : Specialty.values()) {
                assertThat(specialty.getDisplayName())
                        .as("displayName for %s should not be blank", specialty.name())
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("ENT specialty should have the full human-readable displayName")
        void specialty_ent_shouldHaveFullDisplayName() {
            assertThat(Specialty.ENT.getDisplayName()).isEqualTo("Ear, Nose & Throat");
        }

        @Test
        @DisplayName("Specialty.name() returns uppercase (used for EnumType.STRING in DB)")
        void specialty_name_shouldBeUppercase() {
            assertThat(Specialty.CARDIOLOGY.name()).isEqualTo("CARDIOLOGY");
            assertThat(Specialty.GENERAL_MEDICINE.name()).isEqualTo("GENERAL_MEDICINE");
            assertThat(Specialty.ENT.name()).isEqualTo("ENT");
        }

        @Test
        @DisplayName("Specialty.valueOf() should resolve from the stored DB string")
        void specialty_valueOf_shouldResolveFromDbString() {
            // This is how JPA resolves the enum from the VARCHAR column value
            assertThat(Specialty.valueOf("CARDIOLOGY")).isEqualTo(Specialty.CARDIOLOGY);
            assertThat(Specialty.valueOf("GENERAL_SURGERY")).isEqualTo(Specialty.GENERAL_SURGERY);
        }
    }
}
