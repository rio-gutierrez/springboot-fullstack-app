package space.ml_tech.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("beanOfTypeList")
public class CustomerListDataAccessService implements CustomerDao {

    // fake DB
    private static final List<Customer> customers;

    static {
        customers = new ArrayList<>();
        customers.add(new Customer(1, "Alex", "alex@gmail.com", 21));
        customers.add(new Customer(2, "Jamila", "jamila@gmail.com", 19));
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public void deletePersonById(Integer id) {
        customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findAny()
                .ifPresent(customers::remove);
    }

    @Override
    public void updatePerson(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream()
                .anyMatch(customer -> customer.getId().equals(id));
    }
}
