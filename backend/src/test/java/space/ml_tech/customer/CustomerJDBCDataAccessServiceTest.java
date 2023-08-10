package space.ml_tech.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import space.ml_tech.AbstractTestContainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService customerJDBCDataAccessService;
    private String email;
    private Customer customer;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        customerJDBCDataAccessService = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }


    // ------ GET methods ------ //

    @Test
    @DisplayName("Test that all customers can be selected")
    void selectAllCustomers() {
        // Given
        injectCustomer();

        // When
        List<Customer> customers = customerJDBCDataAccessService.selectAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    @DisplayName("Test that we can select a customer given their id")
    void selectCustomerById() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();

        // When
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        actual.ifPresent(c -> System.out.println("customer id in selectCustomerById() method: " + c.getId()));


        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    @DisplayName("Test that given an invalid id, no customer will be returned")
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int wrongId = -1;

        // When
        var actual = customerJDBCDataAccessService.selectCustomerById(wrongId);

        // Then
        assertThat(actual).isEmpty();
    }


    // ------ POST methods ------ //


    @Test
    @DisplayName("Test that inserting a new customer to our  database works")
    void insertCustomer() {
        // Given
        // <customer built in `injectCustomer()` method>

        // When
        injectCustomer();

        // Then
        assertThat(customerJDBCDataAccessService.selectAllCustomers()).isNotEmpty();
    }


    // ------ PUT methods ------ //

    @Test
    @DisplayName("Test that we can update a customer's name")
    void updatePersonByName() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();
        String newName = "New Name";

        // When
        customer.setId(customerId);
        customer.setName(newName);
        customerJDBCDataAccessService.updatePerson(customer);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    @DisplayName("Test that we can update a customer's email")
    void updatePersonByEmail() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        customer.setId(customerId);
        customer.setEmail(newEmail);
        customerJDBCDataAccessService.updatePerson(customer);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    @DisplayName("Test that we can update a customer's age")
    void updatePersonByAge() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();
        int newAge = 100;

        // When
        customer.setId(customerId);
        customer.setAge(newAge);
        customerJDBCDataAccessService.updatePerson(customer);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    @DisplayName("Test that we can update all of a customer's properties")
    void updateAllPropertiesForPerson() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();
        String newName = "FooBar";
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        int newAge = 110;

        // When
        customer.setId(customerId);
        customer.setName(newName);
        customer.setEmail(newEmail);
        customer.setAge(newAge);
        customerJDBCDataAccessService.updatePerson(customer);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).isPresent().hasValue(customer);
    }

    @Test
    @DisplayName("Test that no updates will take place if no customer's properties have changed")
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();

        // When
        customerJDBCDataAccessService.updatePerson(customer);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    // ------ DELETE methods ------ //

    @Test
    @DisplayName("Test that after deleting a customer" +
            " they're indeed gone from our database")
    void deletePersonById() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();

        // When
        customerJDBCDataAccessService.deletePersonById(customerId);

        // Then
        var actual = customerJDBCDataAccessService.selectCustomerById(customerId);
        assertThat(actual).isNotPresent();
    }


    // ------ MISC methods ------ //

    @Test
    @DisplayName("Test that given a customer with a particular email" +
            " the customer exists")
    void existsPersonWithEmail() {
        // Given
        injectCustomer();

        // When
        boolean actual = customerJDBCDataAccessService.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isTrue();
    }


    @Test
    @DisplayName("Test that given a random new email" +
            " the customer doesn't exist in our database ")
    void personWithRandomNewEmailDoesNotExist() {
        // Given
        String randomEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = customerJDBCDataAccessService.existsPersonWithEmail(randomEmail);

        // Then
        assertThat(actual).isFalse();
    }


    @Test
    @DisplayName("Test that given a customer with a particular id" +
            " the customer exists")
    void existsPersonWithId() {
        // Given
        injectCustomer();
        int customerId = getCustomerId();

        // When
        boolean actual = customerJDBCDataAccessService.existsPersonWithId(customerId);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Test that given a random new id" +
            " the customer doesn't exist in our database ")
    void personWithRandomNewIdDoesNotExist() {
        // Given
        int wrongId = -1;

        // When
        boolean actual = customerJDBCDataAccessService.existsPersonWithId(wrongId);

        // Then
        assertThat(actual).isFalse();
    }


    // ---- HELPER METHODS ---- //
    void injectCustomer() {
        email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        customer = Customer.builder()
                .name(FAKER.name().fullName())
                .email(email)
                .age(20)
                .gender(Gender.MALE)
                .build();

        customerJDBCDataAccessService.insertCustomer(customer);
    }

    int getCustomerId() {
        return customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();
    }

}