package co.parameta.technical.test.rest.configuration;

import co.parameta.technical.test.commons.pojo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Value("${soap.service.endpoint}")
    private String soapServiceUrl;

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


    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setDefaultUri(soapServiceUrl);
        return webServiceTemplate;
    }

}
