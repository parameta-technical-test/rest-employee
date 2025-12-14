package co.parameta.technical.test.rest.configuration;

import co.parameta.technical.test.commons.pojo.AdministratorUserPojo;
import co.parameta.technical.test.commons.pojo.EmployeePojo;
import co.parameta.technical.test.commons.pojo.EmployeeResponsePojo;
import co.parameta.technical.test.commons.pojo.PositionPojo;
import co.parameta.technical.test.commons.pojo.SaveEmployeeRequestPojo;
import co.parameta.technical.test.commons.pojo.TypeDocumentPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * SOAP client configuration.
 * <p>
 * This configuration defines the JAXB marshaller and the {@link WebServiceTemplate}
 * used to communicate with the external SOAP service responsible for employee
 * operations.
 * </p>
 *
 * <p>
 * The SOAP endpoint URL is injected from application configuration using
 * the {@code soap.service.endpoint} property.
 * </p>
 */
@Configuration
public class SoapClientConfig {

    /**
     * Base URL of the SOAP service endpoint.
     */
    @Value("${soap.service.endpoint}")
    private String soapServiceUrl;

    /**
     * Creates a {@link Jaxb2Marshaller} configured with all JAXB-annotated
     * classes required to marshal and unmarshal SOAP requests and responses.
     *
     * @return configured {@link Jaxb2Marshaller} instance
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                SaveEmployeeRequestPojo.class,
                EmployeePojo.class,
                TypeDocumentPojo.class,
                PositionPojo.class,
                AdministratorUserPojo.class,
                EmployeeResponsePojo.class
        );
        return marshaller;
    }

    /**
     * Creates and configures the {@link WebServiceTemplate} used as the SOAP client.
     * <p>
     * The template is configured with the JAXB marshaller/unmarshaller and
     * the default SOAP endpoint URI.
     * </p>
     *
     * @param marshaller JAXB marshaller used for request/response conversion
     * @return configured {@link WebServiceTemplate} instance
     */
    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setDefaultUri(soapServiceUrl);
        return webServiceTemplate;
    }
}
