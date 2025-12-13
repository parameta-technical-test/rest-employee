package co.parameta.technical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private String user;

    private String token;

    private long expirationTime;

    private String role;

    private String roleDescription;

}
