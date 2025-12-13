package co.parameta.technical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseValidationGroovieDTO {

    private String message;

    private boolean error;

}
