package co.parameta.technical.test.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "LoginRequest",
        description = "Request object used to authenticate a user"
)
public class RequestLoginDTO {

    @Schema(
            description = "User email address",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @Schema(
            description = "User password",
            example = "P@ssw0rd123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters")
    private String password;
}
