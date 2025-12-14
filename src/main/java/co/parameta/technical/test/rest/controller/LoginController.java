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

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication and session management endpoints"
)
public class LoginController {

    private final ITokenBlacklistService iTokenBlacklistService;
    private final IAutenticationService iAutenticationService;

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
            @RequestBody @Valid  RequestLoginDTO request
    ) throws Exception {

        ResponseGeneralDTO respuestaGeneralDTO =
                iAutenticationService.userLogin(request);

        return ResponseEntity
                .status(respuestaGeneralDTO.getStatus())
                .body(respuestaGeneralDTO);
    }

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

        ResponseGeneralDTO respuestaGeneralDTO =
                iAutenticationService.userInformation(authorizationHeader);

        return ResponseEntity
                .status(respuestaGeneralDTO.getStatus())
                .body(respuestaGeneralDTO);
    }

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
                iTokenBlacklistService.revokeToken(authorizationHeader)
        );
    }
}
