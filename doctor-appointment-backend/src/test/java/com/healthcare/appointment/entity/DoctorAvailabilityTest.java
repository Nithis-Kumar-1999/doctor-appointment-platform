package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.enums.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link DoctorAvailability} entity.
 *
 * @author Nithish Kumar
 */
@DisplayName("DoctorAvailability Entity Unit Tests")
class DoctorAvailabilityTest {

    // =========================================================================
    // HELPERS
    // =========================================================================

    private Doctor buildDoctor() {
        User user = User.builder()
                .firstName("Ravi")
                .lastName("Kumar")
                .email("ravi@healthcare.com")
                .password("hashed")
                .role(Role.DOCTOR)
                .build();

        return Doctor.builder()
                .user(user)
                .specialty(Specialty.CARDIOLOGY)
                .qualification("MBBS, MD")
                .experienceYears(8)
                .consultationFee(new BigDecimal("700.00"))
                .phone("9876543210")
                .city("Chennai")
                .build();
    }

    private DoctorAvailability buildAvailability() {
        return DoctorAvailability.builder()
                .doctor(buildDoctor())
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .build();
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderTests {

        @Test
        @DisplayName("Builder should set all fields correctly")
        void builder_withAllFields_shouldSetCorrectValues() {
            DoctorAvailability availability = buildAvailability();

            assertThat(availability.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(availability.getStartTime()).isEqualTo(LocalTime.of(9, 0));
            assertThat(availability.getEndTime()).isEqualTo(LocalTime.of(17, 0));
            assertThat(availability.getSlotDurationMinutes()).isEqualTo(30);
        }

        @Test
        @DisplayName("Builder should set doctor relationship correctly")
        void builder_shouldSetDoctorRelationship() {
            DoctorAvailability availability = buildAvailability();

            assertThat(availability.getDoctor()).isNotNull();
            assertThat(availability.getDoctor().getCity()).isEqualTo("Chennai");
        }

        @Test
        @DisplayName("@Builder.Default should set active=true by default")
        void builder_withoutActiveField_shouldDefaultToTrue() {
            DoctorAvailability availability = DoctorAvailability.builder()
                    .doctor(buildDoctor())
                    .dayOfWeek(DayOfWeek.TUESDAY)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(14, 0))
                    .slotDurationMinutes(30)
                    .build();

            assertThat(availability.isActive())
                    .as("New availability entries should be active by default")
                    .isTrue();
        }

        @Test
        @DisplayName("Builder should allow setting active=false")
        void builder_withActiveFalse_shouldBeInactive() {
            DoctorAvailability availability = DoctorAvailability.builder()
                    .doctor(buildDoctor())
                    .dayOfWeek(DayOfWeek.SATURDAY)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(13, 0))
                    .slotDurationMinutes(30)
                    .active(false)
                    .build();

            assertThat(availability.isActive()).isFalse();
        }
    }

    // =========================================================================
    // SCHEDULE LOGIC TESTS
    // =========================================================================

    @Nested
    @DisplayName("Schedule Fields")
    class ScheduleTests {

        @Test
        @DisplayName("LocalTime should preserve hours and minutes exactly")
        void localTime_shouldPreserveTimeValues() {
            LocalTime start = LocalTime.of(9, 30);
            LocalTime end = LocalTime.of(16, 45);

            DoctorAvailability availability = DoctorAvailability.builder()
                    .doctor(buildDoctor())
                    .dayOfWeek(DayOfWeek.WEDNESDAY)
                    .startTime(start)
                    .endTime(end)
                    .slotDurationMinutes(15)
                    .build();

            assertThat(availability.getStartTime().getHour()).isEqualTo(9);
            assertThat(availability.getStartTime().getMinute()).isEqualTo(30);
            assertThat(availability.getEndTime().getHour()).isEqualTo(16);
            assertThat(availability.getEndTime().getMinute()).isEqualTo(45);
        }

        @Test
        @DisplayName("endTime.isAfter(startTime) should be true for valid availability")
        void endTime_shouldBeAfterStartTime_forValidEntry() {
            DoctorAvailability availability = buildAvailability();

            // This is the rule the service layer enforces —
            // here we verify our test data itself is logically consistent.
            assertThat(availability.getEndTime())
                    .isAfter(availability.getStartTime());
        }

        @Test
        @DisplayName("All seven DayOfWeek values should be usable")
        void dayOfWeek_allJdkValues_shouldBeAssignable() {
            // Verifies that java.time.DayOfWeek covers all scheduling scenarios
            DayOfWeek[] allDays = DayOfWeek.values();
            assertThat(allDays).hasSize(7);
            assertThat(allDays).contains(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
            );
        }
    }

    // =========================================================================
    // EQUALS AND HASHCODE TESTS
    // =========================================================================

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Entity should equal itself")
        void equals_withSelf_shouldBeTrue() {
            DoctorAvailability availability = buildAvailability();
            assertThat(availability).isEqualTo(availability);
        }

        @Test
        @DisplayName("Two unpersisted entries should not be equal")
        void equals_givenBothNullIds_shouldNotBeEqual() {
            DoctorAvailability a1 = buildAvailability();
            DoctorAvailability a2 = buildAvailability();
            assertThat(a1).isNotEqualTo(a2);
        }

        @Test
        @DisplayName("hashCode should be consistent across calls")
        void hashCode_shouldBeConsistent() {
            DoctorAvailability availability = buildAvailability();
            assertThat(availability.hashCode()).isEqualTo(availability.hashCode());
        }
    }

    // =========================================================================
    // TOSTRING SAFETY
    // =========================================================================

    @Test
    @DisplayName("toString() should exclude doctor to prevent lazy loading")
    void toString_shouldNotContainDoctorDetails() {
        DoctorAvailability availability = buildAvailability();
        String result = availability.toString();

        assertThat(result).doesNotContain("ravi@healthcare.com");
        assertThat(result).doesNotContain("CARDIOLOGY");
    }
}
