package co.parameta.technical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object that represents the authentication response.
 * <p>
 * This DTO is returned after a successful authentication and
 * contains the JWT token and basic information about the authenticated user.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    /**
     * Identifier of the authenticated user (e.g. email or username).
     */
    private String user;

    /**
     * JWT token generated after successful authentication.
     */
    private String token;

    /**
     * Remaining validity time of the token in milliseconds.
     */
    private long expirationTime;

    /**
     * Role assigned to the authenticated user.
     */
    private String role;

    /**
     * Human-readable description of the user's role.
     */
    private String roleDescription;

}
