package co.parameta.technical.test.rest.dto;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraInformationDTO {

    private Integer years;

    private Integer months;

    private Integer days;

}
