package space.ml_tech.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import space.ml_tech.customer.Customer;
import space.ml_tech.customer.CustomerDTO;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    // We use a WebTestClient to simulate the actions performed by Postman when testing our APIs
    @Autowired
    private WebTestClient webTestClient;

    private static final String CUSTOMER_URI = "api/v1/customers";
    private static final Random RANDOM_INT = new Random();

    // Create fake random data
    private final Faker faker = new Faker();
    private final String name = faker.name().fullName();
    private final String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID() + "ml-tech.space";
    private final int age = RANDOM_INT.nextInt(1, 100);


    /* ----------------------------
            TEST POST
     ---------------------------- */
    @Test
    void canRegisterCustomer() {
        /* The flow we follow using Postman to test our API for registering
         * a new customer and then making sure the customer exists
         * looks as follows:
         *  - create customer registration request
         *  - get all customers
         *  - make sure that our newly registered customer is in the returned list of customers
         *  - get customer by id
         */

        // Step 1 - Create customer registration request
        CustomerDTO request = new CustomerDTO(name, email, age);

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk();

        // Step 2 - Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Step 3 - Make sure that our newly registered customer is in the returned list of customers
        Customer expectedCustomer = Customer.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();

        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        // Step 4 - Get customer by id
        assert allCustomers != null;
        int customerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(customerId);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }


    /* ----------------------------
            TEST DELETE
     ---------------------------- */
    @Test
    void canDeleteCustomer() {
        /* The flow we follow using Postman to test our API for deleting
         * a customer and then making sure the customer does not exist
         * after deletion looks as follows:
         *  - create customer registration request
         *  - get all customers
         *  - get customer by id
         *  - delete the customer
         *  - test that the customer has indeed been deleted
         */

        // Step 1 - Create customer registration request
        CustomerDTO request = new CustomerDTO(name, email, age);

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk();

        // Step 2 - Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Step 3 - Get customer by id
        assert allCustomers != null;
        int customerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Step 4 - Delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // Step 5 - Test that the customer has indeed been deleted
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    /* ----------------------------
            TEST UPDATE
     ---------------------------- */
    @Test
    void canUpdateCustomer() {
        /* The flow we follow using Postman to test our API for deleting
         * a customer and then making sure the customer does not exist
         * after deletion looks as follows:
         *  - create customer registration request
         *  - get all customers
         *  - get customer by id
         *  - delete the customer
         *  - test that the customer has indeed been deleted
         */

        // Step 1 - Create customer registration request
        CustomerDTO request = new CustomerDTO(name, email, age);

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk();

        // Step 2 - Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Step 4 - Get customer by id
        assert allCustomers != null;
        int customerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Step 5 - Update the customer
        String newName = "Venom Snake";
        CustomerDTO updatedCustomerDto = new CustomerDTO(newName, null, null);

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedCustomerDto), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk();

        // Step 6 - Test that the customer has indeed been updated
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(customerId, newName, email, age);
        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

}
