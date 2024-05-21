package foodblocko.utils;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class MysqlDataUtilsTest {

    @Test
    void getConnection() {
        Connection connection = null;
        try {
            connection = MysqlDataUtils.getConnection();
            assertNotNull(connection, "The connection should not be null");
            assertFalse(connection.isClosed(), "The connection should be open");
        } catch (SQLException e) {
            fail("An SQLException was thrown: " + e.getMessage());
        } finally {
            MysqlDataUtils.closeConnection(connection);
        }
    }

    @Test
    void closeConnection() {
        Connection connection = null;
        try {
            connection = MysqlDataUtils.getConnection();
            assertNotNull(connection, "The connection should not be null");
            MysqlDataUtils.closeConnection(connection);
            assertTrue(connection.isClosed(), "The connection should be closed");
        } catch (SQLException e) {
            fail("An SQLException was thrown: " + e.getMessage());
        }
    }
}
