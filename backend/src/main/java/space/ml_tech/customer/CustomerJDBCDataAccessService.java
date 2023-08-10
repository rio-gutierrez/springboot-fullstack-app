package space.ml_tech.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This service class is used to access data through JDBC, as opposed to JPA.
 * Link to the JDBC documentation can be found
 * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html">here</a>.
 */
@Repository("beanOfTypeJdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    // ----------- FIELDS & CONSTRUCTORS ----------- //
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }


    // ----------- METHODS ----------- //
    @Override
    public List<Customer> selectAllCustomers() {
        String sqlStatement = """
                SELECT id, name, email, age, gender
                FROM customer
                """;
        return jdbcTemplate.query(sqlStatement, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sqlStatement = """
                SELECT id, name, email, age, gender
                FROM customer
                WHERE id = ?
                """;

        return jdbcTemplate.query(sqlStatement,customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sqlStatement = """
                INSERT INTO customer(name, email, age, gender)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(
                sqlStatement,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name()
        );
    }

    @Override
    public void deletePersonById(Integer id) {
        String sqlStatement = """ 
                DELETE
                FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sqlStatement, id);
        System.out.println("Deleted customer with id = " + id + "!!");
    }

    @Override
    public void updatePerson(Customer update) {
        if (update.getName() != null) {
            String sqlStatement = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sqlStatement, update.getName(), update.getId());
        }
        if (update.getAge() != null) {
            String sqlStatement = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sqlStatement, update.getAge(), update.getId());
        }
        if (update.getEmail() != null) {
            String sqlStatement = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sqlStatement, update.getEmail(), update.getId());
        }
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        String sqlStatement = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sqlStatement, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        String sqlStatement = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sqlStatement, Integer.class, id);
        return count != null && count > 0;
    }
}
