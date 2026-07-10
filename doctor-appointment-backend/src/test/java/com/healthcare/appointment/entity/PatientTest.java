package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Gender;
import com.healthcare.appointment.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Patient} entity and {@link Gender} enum.
 *
 * @author Nithish Kumar
 */
@DisplayName("Patient Entity and Gender Enum Unit Tests")
class PatientTest {

    private User buildUser() {
        return User.builder()
                .firstName("Priya")
                .lastName("Sharma")
                .email("priya.sharma@healthcare.com")
                .password("$2a$12$hashedPassword")
                .role(Role.PATIENT)
                .build();
    }

    private Patient buildPatient() {
        return Patient.builder()
                .user(buildUser())
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .gender(Gender.FEMALE)
                .phone("+91-9876543210")
                .address("12, Gandhi Street, Chennai - 600001")
                .bloodGroup("B+")
                .emergencyContact("+91-9123456780")
                .build();
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderTests {

        @Test
        @DisplayName("Builder should set all provided fields correctly")
        void builder_withAllFields_shouldSetCorrectValues() {
            Patient patient = buildPatient();

            assertThat(patient.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 6, 15));
            assertThat(patient.getGender()).isEqualTo(Gender.FEMALE);
            assertThat(patient.getPhone()).isEqualTo("+91-9876543210");
            assertThat(patient.getAddress()).isEqualTo("12, Gandhi Street, Chennai - 600001");
            assertThat(patient.getBloodGroup()).isEqualTo("B+");
            assertThat(patient.getEmergencyContact()).isEqualTo("+91-9123456780");
        }

        @Test
        @DisplayName("Builder should set user relationship correctly")
        void builder_shouldSetUserRelationship() {
            Patient patient = buildPatient();

            assertThat(patient.getUser()).isNotNull();
            assertThat(patient.getUser().getEmail()).isEqualTo("priya.sharma@healthcare.com");
        }

        @Test
        @DisplayName("@Builder.Default should set active=true when not explicitly provided")
        void builder_withoutActiveField_shouldDefaultToTrue() {
            Patient patient = Patient.builder()
                    .user(buildUser())
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .phone("9876543210")
                    .build();

            assertThat(patient.isActive())
                    .as("New patients should be active by default")
                    .isTrue();
        }

        @Test
        @DisplayName("Optional fields should be null when not set")
        void builder_withoutOptionalFields_shouldHaveNullValues() {
            Patient patient = Patient.builder()
                    .user(buildUser())
                    .dateOfBirth(LocalDate.of(1988, 3, 22))
                    .gender(Gender.OTHER)
                    .phone("9876543210")
                    .build();

            assertThat(patient.getAddress()).isNull();
            assertThat(patient.getBloodGroup()).isNull();
            assertThat(patient.getEmergencyContact()).isNull();
        }
    }

    // =========================================================================
    // EQUALS AND HASHCODE TESTS
    // =========================================================================

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Patient should equal itself")
        void equals_withSelf_shouldBeTrue() {
            Patient patient = buildPatient();
            assertThat(patient).isEqualTo(patient);
        }

        @Test
        @DisplayName("Two unpersisted patients (null id) should not be equal")
        void equals_givenBothNullIds_shouldNotBeEqual() {
            Patient p1 = buildPatient();
            Patient p2 = buildPatient();
            assertThat(p1).isNotEqualTo(p2);
        }

        @Test
        @DisplayName("Patient should not equal null")
        void equals_withNull_shouldBeFalse() {
            assertThat(buildPatient()).isNotEqualTo(null);
        }

        @Test
        @DisplayName("hashCode should be consistent")
        void hashCode_shouldBeConsistent() {
            Patient patient = buildPatient();
            assertThat(patient.hashCode()).isEqualTo(patient.hashCode());
        }
    }

    // =========================================================================
    // TOSTRING SAFETY TEST
    // =========================================================================

    @Test
    @DisplayName("toString() should exclude user field to prevent lazy loading")
    void toString_shouldNotContainUserDetails() {
        Patient patient = buildPatient();
        String result = patient.toString();

        assertThat(result).doesNotContain("priya.sharma@healthcare.com");
        assertThat(result).doesNotContain("hashedPassword");
    }

    // =========================================================================
    // GENDER ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("Gender Enum")
    class GenderEnumTests {

        @Test
        @DisplayName("Gender enum should have exactly 3 values")
        void gender_shouldHaveThreeValues() {
            assertThat(Gender.values()).hasSize(3);
        }

        @Test
        @DisplayName("Each Gender should have a non-blank displayName")
        void gender_allValues_shouldHaveNonBlankDisplayName() {
            for (Gender gender : Gender.values()) {
                assertThat(gender.getDisplayName())
                        .as("displayName for %s", gender.name())
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("Gender.name() returns uppercase (for EnumType.STRING persistence)")
        void gender_name_shouldBeUppercase() {
            assertThat(Gender.MALE.name()).isEqualTo("MALE");
            assertThat(Gender.FEMALE.name()).isEqualTo("FEMALE");
            assertThat(Gender.OTHER.name()).isEqualTo("OTHER");
        }
    }
}
