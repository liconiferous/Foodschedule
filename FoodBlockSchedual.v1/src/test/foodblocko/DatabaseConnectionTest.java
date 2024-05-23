package foodblocko;

import foodblocko.utils.MysqlDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    private DatabaseConnection dbConnection;
    private Connection connection;

    @BeforeEach
    void setUp() {
        dbConnection = new DatabaseConnection();
        connection = MysqlDataUtils.getConnection();
        try (Statement statement = connection.createStatement()) {
            // Setup the database for testing
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), email VARCHAR(255))");
            statement.execute("CREATE TABLE IF NOT EXISTS schedule (id INT AUTO_INCREMENT PRIMARY KEY, rowinfo INT, col INT, username VARCHAR(255), dateinfo VARCHAR(255), mealtype VARCHAR(255), mealname VARCHAR(255), recipe TEXT)");
            statement.execute("TRUNCATE TABLE users"); // Clean up users table before each test
            statement.execute("TRUNCATE TABLE schedule"); // Clean up schedule table before each test
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try (Statement statement = connection.createStatement()) {
            // Clean up the database after each test
            statement.execute("DROP TABLE IF EXISTS users");
            statement.execute("DROP TABLE IF EXISTS schedule");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MysqlDataUtils.closeConnection(connection);
        }
    }

    @Test
    void insertUser() {
        dbConnection.insertUser("testUser", "testPass", "testEmail@example.com");
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT * FROM users WHERE username='testUser'")) {
            assertTrue(resultSet.next(), "User should be inserted");
            assertEquals("testUser", resultSet.getString("username"));
            assertEquals("testPass", resultSet.getString("password"));
            assertEquals("testEmail@example.com", resultSet.getString("email"));
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    void saveOrUpdateMeal() {
        dbConnection.saveOrUpdateMeal("2023-01-01", "testUser", 1, 1, "Breakfast", "Pancakes", "Recipe for pancakes");
        ScheduleInfo schedule = dbConnection.queryMealSchedule("2023-01-01", "testUser", 1, 1);
        assertNotNull(schedule, "Schedule should be saved");
        assertEquals("Breakfast", schedule.getMealtype());
        assertEquals("Pancakes", schedule.getMealname());
        assertEquals("Recipe for pancakes", schedule.getRecipe());

        // Test update functionality
        dbConnection.saveOrUpdateMeal("2023-01-01", "testUser", 1, 1, "Breakfast", "Waffles", "Recipe for waffles");
        schedule = dbConnection.queryMealSchedule("2023-01-01", "testUser", 1, 1);
        assertNotNull(schedule, "Schedule should be updated");
        assertEquals("Waffles", schedule.getMealname());
        assertEquals("Recipe for waffles", schedule.getRecipe());
    }

    @Test
    void queryMealSchedule() {
        dbConnection.saveSchedule("2023-01-01", "testUser", 1, 1, "Breakfast", "Pancakes", "Recipe for pancakes");
        ScheduleInfo schedule = dbConnection.queryMealSchedule("2023-01-01", "testUser", 1, 1);
        assertNotNull(schedule, "Schedule should be queried");
        assertEquals("Breakfast", schedule.getMealtype());
        assertEquals("Pancakes", schedule.getMealname());
        assertEquals("Recipe for pancakes", schedule.getRecipe());
    }

    @Test
    void saveSchedule() {
        dbConnection.saveSchedule("2023-01-01", "testUser", 1, 1, "Breakfast", "Pancakes", "Recipe for pancakes");
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT * FROM schedule WHERE username='testUser' AND dateinfo='2023-01-01'")) {
            assertTrue(resultSet.next(), "Schedule should be saved");
            assertEquals(1, resultSet.getInt("rowinfo"));
            assertEquals(1, resultSet.getInt("col"));
            assertEquals("Breakfast", resultSet.getString("mealtype"));
            assertEquals("Pancakes", resultSet.getString("mealname"));
            assertEquals("Recipe for pancakes", resultSet.getString("recipe"));
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    void updateSchedule() {
        dbConnection.saveSchedule("2023-01-01", "testUser", 1, 1, "Breakfast", "Pancakes", "Recipe for pancakes");
        ScheduleInfo schedule = dbConnection.queryMealSchedule("2023-01-01", "testUser", 1, 1);
        assertNotNull(schedule, "Schedule should be queried");

        dbConnection.updateSchedule(schedule.getId(), "Lunch", "Sandwich", "Recipe for sandwich");
        schedule = dbConnection.queryMealSchedule("2023-01-01", "testUser", 1, 1);
        assertEquals("Lunch", schedule.getMealtype());
        assertEquals("Sandwich", schedule.getMealname());
        assertEquals("Recipe for sandwich", schedule.getRecipe());
    }

    @Test
    void loadMealSchedule() {
        dbConnection.saveSchedule("2023-01-01", "testUser", 1, 1, "Breakfast", "Pancakes", "Recipe for pancakes");
        dbConnection.saveSchedule("2023-01-01", "testUser", 2, 1, "Lunch", "Sandwich", "Recipe for sandwich");
        List<ScheduleInfo> schedules = dbConnection.loadMealSchedule("2023-01-01", "testUser");
        assertEquals(2, schedules.size(), "There should be two schedules loaded");
    }

    @Test
    void authenticateUser() {
        dbConnection.insertUser("testUser", "testPass", "testEmail@example.com");
        assertTrue(dbConnection.authenticateUser("testUser", "testPass"), "User should be authenticated");
        assertFalse(dbConnection.authenticateUser("testUser", "wrongPass"), "User should not be authenticated with wrong password");
        assertFalse(dbConnection.authenticateUser("wrongUser", "testPass"), "Non-existent user should not be authenticated");
    }
}
