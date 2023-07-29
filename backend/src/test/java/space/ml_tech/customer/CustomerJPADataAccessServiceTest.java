package space.ml_tech.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService customerJPADataAccessService;
    @Mock
    CustomerRepository customerRepository;
    private AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customerJPADataAccessService =  new CustomerJPADataAccessService(customerRepository);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // When
        customerJPADataAccessService.selectAllCustomers();

        // Then
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // Given
        int randomId = 5;

        // When
        customerJPADataAccessService.selectCustomerById(randomId);

        // Then
        Mockito.verify(customerRepository).findById(randomId);
    }

    @Test
    void insertCustomer() {
        // Given
        Customer customer = new Customer(
                1,
                "Rio",
                "rio_is_the_best@gmail.com",
                35
        );

        // When
        customerJPADataAccessService.insertCustomer(customer);

        // Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void deletePersonById() {
        // Given
        int randomId = 7;

        // When
        customerJPADataAccessService.deletePersonById(randomId);

        // Then
        Mockito.verify(customerRepository).deleteById(randomId);
    }

    @Test
    void updatePerson() {
        // Given
        Customer customer = new Customer(
                2,
                "Alex",
                "alex_is_the_best@gmail.com",
                25
        );

        // When
        customerJPADataAccessService.updatePerson(customer);

        // Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String randomEmail = "foo@icloud.com";

        // When
        customerJPADataAccessService.existsPersonWithEmail(randomEmail);

        // Then
        Mockito.verify(customerRepository).existsCustomerByEmail(randomEmail);
    }

    @Test
    void existsPersonWithId() {
        // Given
        int randomId = 5;

        // When
        customerJPADataAccessService.existsPersonWithId(randomId);

        // Then
        Mockito.verify(customerRepository).existsCustomerById(randomId);
    }
}