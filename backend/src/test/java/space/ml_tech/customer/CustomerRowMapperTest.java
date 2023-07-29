package space.ml_tech.customer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        int testId = 4;
        int testAge = 20;
        String testName = "Foo";
        String testEmail = "foo@bar.com";

        Customer expected = Customer.builder()
                .id(testId)
                .name(testName)
                .email(testEmail)
                .age(testAge)
                .build();

        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = Mockito.mock(ResultSet.class);  // another way of mocking, without using @Mock
        Mockito.when(resultSet.getInt("id")).thenReturn(testId);
        Mockito.when(resultSet.getInt("age")).thenReturn(testAge);
        Mockito.when(resultSet.getString("name")).thenReturn(testName);
        Mockito.when(resultSet.getString("email")).thenReturn(testEmail);

        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual).isEqualTo(expected);
    }
}