package space.ml_tech.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import space.ml_tech.exceptions.DuplicateResourceException;
import space.ml_tech.exceptions.RequestValidationException;
import space.ml_tech.exceptions.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerDao customerDao;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerDao);
    }

    @Test
    void getAllCostumers() {
        // When
        customerService.getAllCostumers();

        // Then
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        // Given
        int randomId = 9;
        Customer customer = new Customer(
                randomId,
                "Foo",
                "foo@bar.com",
                100,
                Gender.MALE
        );
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        // When
        Customer actual = customerService.getCustomer(randomId);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenCustomerWithIdIsNotFound() {
        // Given
        int randomId = 10;
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> customerService.getCustomer(randomId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found!".formatted(randomId));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "foo@bar.com";
        CustomerDTO customerDto = new CustomerDTO("foo", email, 15, Gender.MALE);
        Customer customer = Customer.builder()
                .name(customerDto.name())
                .age(customerDto.age())
                .email(customerDto.email())
                .gender(customerDto.gender())
                .build();
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        // When
        customerService.addCustomer(customerDto);

        // Then
        Mockito.verify(customerDao).insertCustomer(customer);
        assertThat(customer.getId()).isNull();
        assertThat(customer.getName()).isEqualTo(customerDto.name());
        assertThat(customer.getEmail()).isEqualTo(customerDto.email());
        assertThat(customer.getAge()).isEqualTo(customerDto.age());
        assertThat(customer.getGender()).isEqualTo(customerDto.gender());
    }



    @Test
    void willThrowWhenCustomerEmailAlreadyExists() {
        // Given
        String email = "foo@bar.com";
        CustomerDTO customerDto = new CustomerDTO("foo", email, 15, Gender.MALE);
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        // When
        assertThatThrownBy(() -> customerService.addCustomer(customerDto))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("Email already taken!");

        // Then
        // make sure that no customer with duplicate email is ever inserted
        Mockito.verify(customerDao, Mockito.never()).insertCustomer(Mockito.any());
    }


    @Test
    void deleteCustomerById() {
        // Given
        int randomId = 8;
        Mockito.when(customerDao.existsPersonWithId(randomId)).thenReturn(true);

        // When
        customerService.deleteCustomerById(randomId);

        // Then
        Mockito.verify(customerDao).deletePersonById(randomId);
    }


    @Test
    void willThrowWhenDeletingCustomerWithNonexistentId() {
        // Given
        int randomId = 18;
        Mockito.when(customerDao.existsPersonWithId(randomId)).thenReturn(false);

        // When
        assertThatThrownBy(() ->   customerService.deleteCustomerById(randomId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Person with id %s does not exist!".formatted(randomId));

        // Then
        Mockito.verify(customerDao, Mockito.never()).deletePersonById(randomId);
    }



    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        String updatedEmail = "faa@icloud.bar";
        CustomerDTO updateRequest = new CustomerDTO("Faa", updatedEmail, 100, Gender.MALE);
        Customer updatedCustomer = Customer.builder()
                .id(randomId)
                .name(updateRequest.name())
                .email(updateRequest.email())
                .age(updateRequest.age())
                .gender(updateRequest.gender())
                .build();
        Mockito.when(customerDao.existsPersonWithEmail(updatedEmail)).thenReturn(false);

        // When
        customerService.updateCustomer(randomId, updateRequest);

        // Then
        Mockito.verify(customerDao).updatePerson(updatedCustomer);
        assertThat(updatedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(updatedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }


    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        CustomerDTO updateRequest = new CustomerDTO("Faa", null, null, Gender.MALE);
        Customer updatedCustomer = Customer.builder()
                .id(randomId)
                .name(updateRequest.name())
                .email(customer.getEmail())
                .age(customer.getAge())
                .gender(customer.getGender())
                .build();


        // When
        customerService.updateCustomer(randomId, updateRequest);

        // Then
        Mockito.verify(customerDao).updatePerson(updatedCustomer);
        assertThat(updatedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(updatedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(updatedCustomer.getGender()).isEqualTo(customer.getGender());
    }


    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        CustomerDTO updateRequest = new CustomerDTO(null, "faa@bar.io", null, Gender.MALE);
        Customer updatedCustomer = Customer.builder()
                .id(randomId)
                .name(customer.getName())
                .email(updateRequest.email())
                .age(customer.getAge())
                .gender(customer.getGender())
                .build();


        // When
        customerService.updateCustomer(randomId, updateRequest);

        // Then
        Mockito.verify(customerDao).updatePerson(updatedCustomer);
        assertThat(updatedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(updatedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(updatedCustomer.getGender()).isEqualTo(customer.getGender());
    }


    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        CustomerDTO updateRequest = new CustomerDTO(null, null, 120, Gender.MALE);
        Customer updatedCustomer = Customer.builder()
                .id(randomId)
                .name(customer.getName())
                .email(customer.getEmail())
                .age(updateRequest.age())
                .gender(customer.getGender())
                .build();


        // When
        customerService.updateCustomer(randomId, updateRequest);

        // Then
        Mockito.verify(customerDao).updatePerson(updatedCustomer);
        assertThat(updatedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(updatedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(updatedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }


    @Test
    void willThrowWhenTryingToUpdateCustomerWithTakenEmail() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        String updatedEmail = "faa@icloud.bar";
        CustomerDTO updateRequest = new CustomerDTO(null, updatedEmail, null, Gender.MALE);
        Mockito.when(customerDao.existsPersonWithEmail(updatedEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> customerService.updateCustomer(randomId, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("Email already taken!");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updatePerson(Mockito.any());
    }


    @Test
    void willThrowWhenNoChangesMade() {
        // Given
        int randomId = 9;
        Customer customer = Customer.builder()
                .id(randomId)
                .name("Foo")
                .email("foo@bar.com")
                .age(90)
                .gender(Gender.MALE)
                .build();
        Mockito.when(customerDao.selectCustomerById(randomId)).thenReturn(Optional.of(customer));

        CustomerDTO updateRequest = new CustomerDTO(customer.getName(), customer.getEmail(), customer.getAge(), Gender.MALE);

        // When
        assertThatThrownBy(() -> customerService.updateCustomer(randomId, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found.");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updatePerson(Mockito.any());
    }


}