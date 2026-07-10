package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.AppointmentStatus;
import com.healthcare.appointment.enums.Gender;
import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.enums.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Appointment} entity and {@link AppointmentStatus} enum.
 *
 * @author Nithish Kumar
 */
@DisplayName("Appointment Entity and AppointmentStatus Enum Unit Tests")
class AppointmentTest {

    // =========================================================================
    // HELPERS
    // =========================================================================

    private Doctor buildDoctor() {
        User user = User.builder()
                .firstName("Anand")
                .lastName("Raj")
                .email("anand@healthcare.com")
                .password("hashed")
                .role(Role.DOCTOR)
                .build();
        return Doctor.builder()
                .user(user)
                .specialty(Specialty.NEUROLOGY)
                .qualification("MBBS, MD")
                .experienceYears(12)
                .consultationFee(new BigDecimal("900.00"))
                .phone("9876543210")
                .city("Bangalore")
                .build();
    }

    private Patient buildPatient() {
        User user = User.builder()
                .firstName("Meena")
                .lastName("Devi")
                .email("meena@healthcare.com")
                .password("hashed")
                .role(Role.PATIENT)
                .build();
        return Patient.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1992, 4, 10))
                .gender(Gender.FEMALE)
                .phone("9123456789")
                .build();
    }

    private Appointment buildAppointment() {
        return Appointment.builder()
                .doctor(buildDoctor())
                .patient(buildPatient())
                .appointmentDate(LocalDate.of(2026, 8, 15))
                .appointmentTime(LocalTime.of(10, 30))
                .reason("Persistent headaches and dizziness for 2 weeks")
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
            Appointment appointment = buildAppointment();

            assertThat(appointment.getAppointmentDate()).isEqualTo(LocalDate.of(2026, 8, 15));
            assertThat(appointment.getAppointmentTime()).isEqualTo(LocalTime.of(10, 30));
            assertThat(appointment.getReason()).isEqualTo("Persistent headaches and dizziness for 2 weeks");
        }

        @Test
        @DisplayName("@Builder.Default should set status=PENDING when not explicitly provided")
        void builder_withoutStatus_shouldDefaultToPending() {
            Appointment appointment = buildAppointment();

            assertThat(appointment.getStatus())
                    .as("New appointments should default to PENDING status")
                    .isEqualTo(AppointmentStatus.PENDING);
        }

        @Test
        @DisplayName("Builder should allow overriding default status")
        void builder_withExplicitStatus_shouldUseProvidedStatus() {
            Appointment appointment = Appointment.builder()
                    .doctor(buildDoctor())
                    .patient(buildPatient())
                    .appointmentDate(LocalDate.of(2026, 9, 1))
                    .appointmentTime(LocalTime.of(14, 0))
                    .status(AppointmentStatus.CONFIRMED)
                    .reason("Follow-up consultation")
                    .build();

            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Optional fields (notes, cancellationReason) should be null when not set")
        void builder_withoutOptionalFields_shouldHaveNullValues() {
            Appointment appointment = buildAppointment();

            assertThat(appointment.getNotes()).isNull();
            assertThat(appointment.getCancellationReason()).isNull();
        }

        @Test
        @DisplayName("Builder should set doctor and patient relationships correctly")
        void builder_shouldSetRelationshipsCorrectly() {
            Appointment appointment = buildAppointment();

            assertThat(appointment.getDoctor()).isNotNull();
            assertThat(appointment.getPatient()).isNotNull();
            assertThat(appointment.getDoctor().getCity()).isEqualTo("Bangalore");
            assertThat(appointment.getPatient().getPhone()).isEqualTo("9123456789");
        }

        @Test
        @DisplayName("Setter should allow updating status after creation (lifecycle transitions)")
        void setter_shouldAllowStatusUpdate() {
            Appointment appointment = buildAppointment();
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);

            appointment.setStatus(AppointmentStatus.CONFIRMED);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);

            appointment.setStatus(AppointmentStatus.COMPLETED);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        }

        @Test
        @DisplayName("Setter should allow setting cancellationReason when cancelled")
        void setter_shouldAllowSettingCancellationReason() {
            Appointment appointment = buildAppointment();

            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setCancellationReason("Patient requested cancellation");

            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
            assertThat(appointment.getCancellationReason()).isEqualTo("Patient requested cancellation");
        }
    }

    // =========================================================================
    // EQUALS AND HASHCODE
    // =========================================================================

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Appointment should equal itself")
        void equals_withSelf_shouldBeTrue() {
            Appointment a = buildAppointment();
            assertThat(a).isEqualTo(a);
        }

        @Test
        @DisplayName("Two unpersisted appointments should not be equal")
        void equals_givenBothNullIds_shouldNotBeEqual() {
            Appointment a1 = buildAppointment();
            Appointment a2 = buildAppointment();
            assertThat(a1).isNotEqualTo(a2);
        }

        @Test
        @DisplayName("Appointment should not equal null")
        void equals_withNull_shouldBeFalse() {
            assertThat(buildAppointment()).isNotEqualTo(null);
        }

        @Test
        @DisplayName("hashCode should be consistent")
        void hashCode_shouldBeConsistent() {
            Appointment a = buildAppointment();
            assertThat(a.hashCode()).isEqualTo(a.hashCode());
        }
    }

    // =========================================================================
    // TOSTRING SAFETY
    // =========================================================================

    @Test
    @DisplayName("toString() should exclude doctor and patient to prevent lazy loading")
    void toString_shouldNotContainRelationshipDetails() {
        Appointment appointment = buildAppointment();
        String result = appointment.toString();

        assertThat(result).doesNotContain("anand@healthcare.com");
        assertThat(result).doesNotContain("meena@healthcare.com");
        assertThat(result).doesNotContain("hashed");
    }

    // =========================================================================
    // APPOINTMENTSTATUS ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("AppointmentStatus Enum")
    class AppointmentStatusTests {

        @Test
        @DisplayName("Enum should contain exactly 4 statuses")
        void status_shouldHaveFourValues() {
            assertThat(AppointmentStatus.values()).hasSize(4);
        }

        @Test
        @DisplayName("Each status should have a non-blank displayName")
        void status_allValues_shouldHaveNonBlankDisplayName() {
            for (AppointmentStatus status : AppointmentStatus.values()) {
                assertThat(status.getDisplayName())
                        .as("displayName for %s", status.name())
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("Status names should match expected strings for DB storage")
        void status_name_shouldReturnCorrectDbStorageValue() {
            assertThat(AppointmentStatus.PENDING.name()).isEqualTo("PENDING");
            assertThat(AppointmentStatus.CONFIRMED.name()).isEqualTo("CONFIRMED");
            assertThat(AppointmentStatus.COMPLETED.name()).isEqualTo("COMPLETED");
            assertThat(AppointmentStatus.CANCELLED.name()).isEqualTo("CANCELLED");
        }

        @Test
        @DisplayName("Status displayNames should match expected UI labels")
        void status_displayNames_shouldMatchUiLabels() {
            assertThat(AppointmentStatus.PENDING.getDisplayName()).isEqualTo("Pending");
            assertThat(AppointmentStatus.CONFIRMED.getDisplayName()).isEqualTo("Confirmed");
            assertThat(AppointmentStatus.COMPLETED.getDisplayName()).isEqualTo("Completed");
            assertThat(AppointmentStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
        }

        @Test
        @DisplayName("AppointmentStatus.valueOf() should resolve from DB-stored string")
        void status_valueOf_shouldResolveFromDbString() {
            assertThat(AppointmentStatus.valueOf("PENDING")).isEqualTo(AppointmentStatus.PENDING);
            assertThat(AppointmentStatus.valueOf("CANCELLED")).isEqualTo(AppointmentStatus.CANCELLED);
        }
    }
}
