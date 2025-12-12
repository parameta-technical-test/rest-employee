package co.parameta.tecnical.test.rest;

import co.parameta.tecnical.test.commons.configuration.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(
		basePackages = "co.parameta.tecnical.test"
)
@Import({ApplicationConfig.class})
public class RestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

}
