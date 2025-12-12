package co.parameta.tecnical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private String usuario;

    private String token;

    private long tiempoExpiracion;

    private String rol;

    private String descripcionRol;

}
