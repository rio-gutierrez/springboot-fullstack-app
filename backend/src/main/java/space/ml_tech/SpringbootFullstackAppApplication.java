package space.ml_tech;

import space.ml_tech.customer.Customer;
import space.ml_tech.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class SpringbootFullstackAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringbootFullstackAppApplication.class, args);
	}

	@Bean
	CommandLineRunner runner (CustomerRepository customerRepository) {
		return args -> {

			var faker = new Faker();
			var name = faker.name();
			var email = faker.internet().emailAddress();

			var randomAge = new Random();

			Customer customer = Customer.builder()
					.name(name.fullName())
					.email(email)
					.age(randomAge.nextInt(16, 99))
					.build();

			customerRepository.save(customer);
		};
	}
}