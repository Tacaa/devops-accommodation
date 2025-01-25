package com.devops.devops_accommodation.integration;



import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test") // Activates the test profile
@Transactional // Ensures the database state is rolled back after each test
public class DatabaseTest {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testDatabaseConnection() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      assertNotNull(connection);
      System.out.println("Successfully connected to the database!");
    }
  }

  @Test
  public void testInsertAndFetchData() {
    jdbcTemplate.execute("INSERT INTO test_table (id, name) VALUES (1, 'Test User')");
    String name = jdbcTemplate.queryForObject(
        "SELECT name FROM test_table WHERE id = 1", String.class);
    assertEquals("Test User", name);
  }
}
