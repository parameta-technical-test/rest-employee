package co.parameta.technical.test.rest.dto;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object that represents additional calculated information
 * expressed in years, months, and days.
 * <p>
 * This DTO is commonly used to represent values such as:
 * <ul>
 *     <li>Employee age</li>
 *     <li>Time linked to the company</li>
 * </ul>
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraInformationDTO {

    /**
     * Number of complete years.
     */
    private Integer years;

    /**
     * Number of complete months.
     */
    private Integer months;

    /**
     * Number of remaining days.
     */
    private Integer days;

}
