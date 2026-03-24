package nttdata.bootcamp.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap for the Customer microservice.
 */
@SpringBootApplication
public class CustomerServiceApplication {

	/**
	 * @param args standard Spring Boot arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
