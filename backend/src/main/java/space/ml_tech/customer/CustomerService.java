package space.ml_tech.customer;

import space.ml_tech.exceptions.DuplicateResourceException;
import space.ml_tech.exceptions.RequestValidationException;
import space.ml_tech.exceptions.ResourceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("beanOfTypeJdbc") CustomerDao customerDao){
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCostumers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "customer with id %s not found!".formatted(id)
                        )
                );
    }

    public void addCustomer(@NotNull CustomerDTO customerDTO) {

        // make sure customer doesn't already exist...
        // we check this by checking their email
        checkEmailDuplicate(customerDTO);

        // else add the customer to database
        Customer customer = Customer.builder()
                .name(customerDTO.name())
                .age(customerDTO.age())
                .email(customerDTO.email())
                .build();
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        // make sure the person with `id` actually exists
        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException(
                    String.format("Person with id %s does not exist!".formatted(id))
            );
        }
        // if it does exist, then delete them
        customerDao.deletePersonById(id);
    }

    public void updateCustomer(Integer id, CustomerDTO customerDTO) {

        // Instead of using the `getCustomer()` method we implemented above,
        // we could use JPA via `customerRepository.getReferenceById(id)`
        // on one of the `CustomerDao` implementations
        Customer customer = getCustomer(id);

        boolean changes = false;

        // update name, if changed
        if (customerDTO.name() != null && !customerDTO.name().equals(customer.getName())) {
            customer.setName(customerDTO.name());
            changes = true;
        }

        // update email, if changed
        if (customerDTO.email() != null && !customerDTO.email().equals(customer.getEmail())) {

            // make sure email is not already taken
            checkEmailDuplicate(customerDTO);

            customer.setEmail(customerDTO.email());
            changes = true;
        }

        // update age, if changed
        if (customerDTO.age() != null && !customerDTO.age().equals(customer.getAge())) {
            customer.setAge(customerDTO.age());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found.");
        }

        customerDao.updatePerson(customer);
    }


    /*---- Helper Methods -----*/
    public void checkEmailDuplicate(@NotNull CustomerDTO customerDTO) {
        if (customerDao.existsPersonWithEmail(customerDTO.email()))
            throw new DuplicateResourceException("Email already taken!");
    }

}