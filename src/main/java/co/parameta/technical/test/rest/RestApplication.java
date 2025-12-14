package co.parameta.technical.test.rest;

import co.parameta.technical.test.commons.configuration.ApplicationConfig;
import co.parameta.technical.test.commons.configuration.AwsS3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(
		basePackages = "co.parameta.technical.test"
)
@Import({ApplicationConfig.class, AwsS3Config.class})
@EnableAsync
public class RestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

}
