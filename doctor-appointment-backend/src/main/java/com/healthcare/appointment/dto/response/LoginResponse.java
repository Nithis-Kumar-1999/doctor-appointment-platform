package com.healthcare.appointment.dto.response;

/**
 * Response DTO returned after a successful login.
 *
 * <p>Contains the JWT access token, token metadata, and the authenticated
 * user's basic profile — enough for the frontend to initialise the user
 * session without a separate profile API call.
 *
 * <p>The {@code refreshToken} field is populated when the Refresh Token
 * feature is implemented (JWT commit). It is included in this DTO now
 * to avoid a breaking API contract change later.
 *
 * @param accessToken  the JWT access token — sent in the Authorization header
 *                     as {@code Bearer <token>} on every subsequent request
 * @param refreshToken the opaque refresh token — used to obtain a new access
 *                     token when the current one expires
 * @param tokenType    always {@code "Bearer"} — informs the client of the scheme
 * @param userId       the authenticated user's database id
 * @param email        the authenticated user's email
 * @param firstName    the authenticated user's first name (for UI greeting)
 * @param role         the authenticated user's role string (e.g., {@code "DOCTOR"})
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long userId,
        String email,
        String firstName,
        String role
) {}
