package space.ml_tech.customer;

import java.util.List;
import java.util.Optional;

/**
 * This class is not needed for basic CRUD operations
 * that we can simply use the {@link CustomerRepository} DAO for.
 * It's here only for illustrative purposes to see different implementations
 * of data access
 */
public interface CustomerDao {

        // GET methods
        List<Customer> selectAllCustomers();
        Optional<Customer> selectCustomerById(Integer id);

        // POST methods
        void insertCustomer(Customer customer);

        // DELETE methods
        void deletePersonById(Integer id);

        // PUT methods
        void updatePerson(Customer customer);

        // Misc methods
        boolean existsPersonWithEmail(String email);
        boolean existsPersonWithId(Integer id);
}