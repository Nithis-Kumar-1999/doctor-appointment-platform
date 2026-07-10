package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link User} entity.
 *
 * <p><b>What we test at the entity layer:</b>
 * <ul>
 *   <li>Builder pattern produces correct field values.</li>
 *   <li>{@code @Builder.Default} correctly initialises {@code active = true}.</li>
 *   <li>{@code equals()} and {@code hashCode()} behave correctly for JPA entity lifecycle.</li>
 *   <li>The {@code toString()} output does NOT contain the password (security guarantee).</li>
 *   <li>{@link Role} enum has the correct display names.</li>
 * </ul>
 *
 * <p><b>What we do NOT test here:</b>
 * <ul>
 *   <li>Database persistence (belongs in a repository integration test).</li>
 *   <li>Jakarta Validation (belongs in a controller/service integration test with a Validator).</li>
 *   <li>JPA auditing (belongs in an integration test with a Spring context).</li>
 * </ul>
 *
 * @author Nithish Kumar
 */
@DisplayName("User Entity Unit Tests")
class UserTest {

    // =========================================================================
    // HELPER: builds a standard test user
    // =========================================================================
    private User buildTestUser() {
        return User.builder()
                .firstName("Nithish")
                .lastName("Kumar")
                .email("nithish@healthcare.com")
                .password("$2a$12$hashedPasswordHere")
                .role(Role.PATIENT)
                .build();
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderTests {

        @Test
        @DisplayName("Builder should correctly set all provided fields")
        void builder_withAllFields_shouldSetCorrectValues() {
            // WHEN
            User user = buildTestUser();

            // THEN
            assertThat(user.getFirstName()).isEqualTo("Nithish");
            assertThat(user.getLastName()).isEqualTo("Kumar");
            assertThat(user.getEmail()).isEqualTo("nithish@healthcare.com");
            assertThat(user.getPassword()).isEqualTo("$2a$12$hashedPasswordHere");
            assertThat(user.getRole()).isEqualTo(Role.PATIENT);
        }

        @Test
        @DisplayName("@Builder.Default should set active=true even when not explicitly provided")
        void builder_withoutActiveField_shouldDefaultToTrue() {
            // GIVEN: Builder called WITHOUT explicitly setting active
            User user = User.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .email("jane@healthcare.com")
                    .password("hashed")
                    .role(Role.DOCTOR)
                    .build(); // active NOT set

            // THEN: Must be true (proves @Builder.Default works)
            assertThat(user.isActive())
                    .as("New users should be active by default")
                    .isTrue();
        }

        @Test
        @DisplayName("Builder should allow explicitly setting active=false")
        void builder_withActiveFalse_shouldSetDeactivated() {
            // GIVEN
            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@healthcare.com")
                    .password("hashed")
                    .role(Role.PATIENT)
                    .active(false)
                    .build();

            // THEN
            assertThat(user.isActive()).isFalse();
        }

        @Test
        @DisplayName("NoArgsConstructor should produce an entity with active=false (JPA default — expected)")
        void noArgsConstructor_shouldProduceEntityWithDefaultPrimitiveBoolean() {
            // NOTE: @Builder.Default does NOT apply to the no-args constructor.
            // JPA uses the no-args constructor when hydrating entities from the DB.
            // At that point, Hibernate sets field values from the ResultSet — so
            // the primitive default (false) here is overwritten by the actual DB value.
            // This test documents the known, expected behavior.
            User user = new User();

            // active will be the Java primitive boolean default (false)
            // This is acceptable because Hibernate always overwrites it from the DB column.
            assertThat(user.isActive()).isFalse();
        }
    }

    // =========================================================================
    // EQUALS AND HASHCODE TESTS
    // =========================================================================

    @Nested
    @DisplayName("equals() and hashCode() — Business Key Strategy")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Two users with the same email should be equal")
        void equals_givenSameEmail_shouldBeEqual() {
            User user1 = User.builder().email("same@healthcare.com").role(Role.PATIENT).build();
            User user2 = User.builder().email("same@healthcare.com").role(Role.ADMIN).build();

            assertThat(user1).isEqualTo(user2);
        }

        @Test
        @DisplayName("Two users with different emails should NOT be equal")
        void equals_givenDifferentEmails_shouldNotBeEqual() {
            User user1 = User.builder().email("alice@healthcare.com").role(Role.PATIENT).build();
            User user2 = User.builder().email("bob@healthcare.com").role(Role.PATIENT).build();

            assertThat(user1).isNotEqualTo(user2);
        }

        @Test
        @DisplayName("Email comparison should be case-insensitive")
        void equals_givenEmailWithDifferentCase_shouldBeEqual() {
            User user1 = User.builder().email("NITHISH@Healthcare.COM").role(Role.DOCTOR).build();
            User user2 = User.builder().email("nithish@healthcare.com").role(Role.DOCTOR).build();

            assertThat(user1).isEqualTo(user2);
        }

        @Test
        @DisplayName("User should equal itself (reflexive property)")
        void equals_withSelf_shouldBeTrue() {
            User user = buildTestUser();
            assertThat(user).isEqualTo(user);
        }

        @Test
        @DisplayName("User should NOT equal null")
        void equals_withNull_shouldBeFalse() {
            User user = buildTestUser();
            assertThat(user).isNotEqualTo(null);
        }

        @Test
        @DisplayName("User should NOT equal an object of different type")
        void equals_withDifferentType_shouldBeFalse() {
            User user = buildTestUser();
            assertThat(user).isNotEqualTo("a plain string");
        }

        @Test
        @DisplayName("hashCode should be consistent across multiple calls")
        void hashCode_shouldBeConsistentAcrossMultipleCalls() {
            User user = buildTestUser();
            int firstCall  = user.hashCode();
            int secondCall = user.hashCode();
            assertThat(firstCall).isEqualTo(secondCall);
        }

        @Test
        @DisplayName("Equal users should have the same hashCode")
        void hashCode_forEqualUsers_shouldBeSame() {
            User user1 = User.builder().email("same@healthcare.com").role(Role.PATIENT).build();
            User user2 = User.builder().email("same@healthcare.com").role(Role.DOCTOR).build();

            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }
    }

    // =========================================================================
    // TOSTRING SECURITY TEST
    // =========================================================================

    @Nested
    @DisplayName("toString() — Security")
    class ToStringTests {

        @Test
        @DisplayName("toString() should NOT contain the password field")
        void toString_shouldExcludePassword() {
            // GIVEN
            User user = User.builder()
                    .firstName("Nithish")
                    .lastName("Kumar")
                    .email("nithish@healthcare.com")
                    .password("super_secret_bcrypt_hash")
                    .role(Role.PATIENT)
                    .build();

            // WHEN
            String result = user.toString();

            // THEN: Password must never appear in logs
            assertThat(result).doesNotContain("super_secret_bcrypt_hash");
            assertThat(result).doesNotContain("password");
        }

        @Test
        @DisplayName("toString() should contain non-sensitive identifying fields")
        void toString_shouldContainIdentifyingFields() {
            User user = buildTestUser();
            String result = user.toString();

            // Safe fields should appear for debugging
            assertThat(result).contains("Nithish");
            assertThat(result).contains("nithish@healthcare.com");
        }
    }

    // =========================================================================
    // ROLE ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("Role Enum")
    class RoleEnumTests {

        @Test
        @DisplayName("ADMIN role should have correct display name")
        void role_admin_shouldHaveCorrectDisplayName() {
            assertThat(Role.ADMIN.getDisplayName()).isEqualTo("Administrator");
        }

        @Test
        @DisplayName("DOCTOR role should have correct display name")
        void role_doctor_shouldHaveCorrectDisplayName() {
            assertThat(Role.DOCTOR.getDisplayName()).isEqualTo("Doctor");
        }

        @Test
        @DisplayName("PATIENT role should have correct display name")
        void role_patient_shouldHaveCorrectDisplayName() {
            assertThat(Role.PATIENT.getDisplayName()).isEqualTo("Patient");
        }

        @Test
        @DisplayName("Role.name() should return uppercase string (used for EnumType.STRING persistence)")
        void role_name_shouldReturnUppercaseString() {
            assertThat(Role.ADMIN.name()).isEqualTo("ADMIN");
            assertThat(Role.DOCTOR.name()).isEqualTo("DOCTOR");
            assertThat(Role.PATIENT.name()).isEqualTo("PATIENT");
        }

        @Test
        @DisplayName("Role.values() should contain exactly 3 roles")
        void role_values_shouldContainExactlyThreeRoles() {
            assertThat(Role.values()).hasSize(3);
        }
    }
}
