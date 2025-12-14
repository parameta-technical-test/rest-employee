package co.parameta.technical.test.rest.controller;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.service.ITokenBlacklistService;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;
import co.parameta.technical.test.rest.service.IAutenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for authentication and session management.
 * <p>
 * This controller provides endpoints to authenticate users, retrieve
 * authenticated user information, and invalidate JWT tokens (logout).
 * </p>
 *
 * <p>
 * JWT Bearer authentication is used to secure protected endpoints.
 * </p>
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication and session management endpoints"
)
public class LoginController {

    private final ITokenBlacklistService tokenBlacklistService;
    private final IAutenticationService authenticationService;

    /**
     * Authenticates a user and generates a JWT token.
     * <p>
     * The credentials provided in the request body are validated and,
     * if successful, a JWT token is returned in the response.
     * </p>
     *
     * @param request login credentials (email and password)
     * @return response containing authentication result and JWT token
     * @throws Exception if an unexpected authentication error occurs
     */
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = ResponseGeneralDTO.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content
    )
    @PostMapping("/autentication")
    public ResponseEntity<ResponseGeneralDTO> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RequestLoginDTO.class))
            )
            @RequestBody @Valid RequestLoginDTO request
    ) throws Exception {

        ResponseGeneralDTO response =
                authenticationService.userLogin(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    /**
     * Retrieves information of the authenticated user.
     * <p>
     * The user is identified using the JWT token provided in the
     * {@code Authorization} header.
     * </p>
     *
     * @param authorizationHeader JWT Authorization header (Bearer token)
     * @return response containing authenticated user information
     * @throws Exception if the token is invalid or an error occurs
     */
    @Operation(
            summary = "Get user information",
            description = "Returns authenticated user information based on JWT token"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = ResponseGeneralDTO.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized or invalid token",
            content = @Content
    )
    @GetMapping("/information")
    public ResponseEntity<ResponseGeneralDTO> informationUser(
            @Parameter(
                    in = ParameterIn.HEADER,
                    name = HttpHeaders.AUTHORIZATION,
                    description = "JWT Authorization token (Bearer token)",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) throws Exception {

        ResponseGeneralDTO response =
                authenticationService.userInformation(authorizationHeader);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    /**
     * Logs out the authenticated user by invalidating the JWT token.
     * <p>
     * The token is added to a blacklist so it cannot be used again.
     * </p>
     *
     * @param authorizationHeader JWT Authorization header to revoke
     * @return response indicating the logout result
     */
    @Operation(
            summary = "Logout user",
            description = "Invalidates the current JWT token"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "User logged out successfully",
            content = @Content(schema = @Schema(implementation = ResponseGeneralDTO.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized or invalid token",
            content = @Content
    )
    @GetMapping("/log-out")
    public ResponseEntity<ResponseGeneralDTO> logOut(
            @Parameter(
                    in = ParameterIn.HEADER,
                    name = HttpHeaders.AUTHORIZATION,
                    description = "JWT Authorization token to revoke",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                tokenBlacklistService.revokeToken(authorizationHeader)
        );
    }
}
