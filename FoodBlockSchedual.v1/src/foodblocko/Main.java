package foodblocko;

import foodblocko.utils.MysqlDataUtils;


import java.sql.*;
import java.util.*;
import javax.swing.*;


class DatabaseConnection {
    public void insertUser(String name, String password, String email) {
        Connection connection = MysqlDataUtils.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            int newRow = preparedStatement.executeUpdate();
            System.out.println("new line is" + newRow);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                System.out.println("Database does not exist");
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    MysqlDataUtils.closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveOrUpdateMeal(String dateInfo, String userName, int rowinfo, int column, String mealType, String mealName, String recipe) {
        ScheduleInfo scheduleInfo = queryMealSchedule(dateInfo, userName, rowinfo, column);
        if (scheduleInfo == null) {
            saveSchedule(dateInfo, userName, rowinfo, column, mealType, mealName, recipe);
        } else {
            updateSchedule(scheduleInfo.getId(), mealType, mealName, recipe);
        }
    }

    public ScheduleInfo queryMealSchedule(String dateInfo, String userName, int rowinfo, int column) {
        ScheduleInfo schedule = null;
        Connection connection = MysqlDataUtils.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT id, rowinfo, col, username, dateinfo, mealtype, mealname, recipe FROM schedule WHERE dateinfo = '%s' and username = '%s' and rowinfo = %d and col = %d", dateInfo, userName, rowinfo, column));
            while (resultSet.next()) {
                if (schedule == null) {
                    schedule = new ScheduleInfo();
                }
                schedule.setId(resultSet.getInt("id"));
                schedule.setRow(resultSet.getInt("rowinfo"));
                schedule.setCol(resultSet.getInt("col"));
                schedule.setUsername(resultSet.getString("username"));
                schedule.setDateinfo(resultSet.getString("dateinfo"));
                schedule.setMealtype(resultSet.getString("mealtype"));
                schedule.setMealname(resultSet.getString("mealname"));
                schedule.setRecipe(resultSet.getString("recipe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            schedule = null;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) MysqlDataUtils.closeConnection(connection);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return schedule;
    }

    public void saveSchedule(String dateInfo, String userName, int rowinfo, int column, String mealType, String mealName, String recipe) {
        Connection connection = MysqlDataUtils.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String query = "INSERT INTO schedule (rowinfo, col, username,dateinfo,mealtype,mealname,recipe) VALUES (?, ?, ?,?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, rowinfo);
            preparedStatement.setInt(2, column);
            preparedStatement.setString(3, userName);
            preparedStatement.setString(4, dateInfo);
            preparedStatement.setString(5, mealType);
            preparedStatement.setString(6, mealName);
            preparedStatement.setString(7, recipe);
            int newRow = preparedStatement.executeUpdate();
            System.out.println("new line is" + newRow);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                System.out.println("Database does not exist");
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    MysqlDataUtils.closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSchedule(int id, String mealType, String mealName, String recipe) {
        Connection connection = MysqlDataUtils.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String query = String.format("update schedule set mealType = '%s' ,mealName= '%s',recipe = '%s' where id = %d", mealType, mealName, recipe, id);
            preparedStatement = connection.prepareStatement(query);
            int newRow = preparedStatement.executeUpdate();
            System.out.println("new line is" + newRow);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                System.out.println("Database does not exist");
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    MysqlDataUtils.closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ScheduleInfo> loadMealSchedule(String dateInfo, String userName) {
        List<ScheduleInfo> list = new ArrayList<>();
        Connection connection = MysqlDataUtils.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT id, rowinfo, col, username, dateinfo, mealtype, mealname, recipe FROM schedule WHERE dateinfo = '%s' and username = '%s'", dateInfo, userName));
            while (resultSet.next()) {
                ScheduleInfo schedule = new ScheduleInfo();
                schedule.setId(resultSet.getInt("id"));
                schedule.setRow(resultSet.getInt("rowinfo"));
                schedule.setCol(resultSet.getInt("col"));
                schedule.setUsername(resultSet.getString("username"));
                schedule.setDateinfo(resultSet.getString("dateinfo"));
                schedule.setMealtype(resultSet.getString("mealtype"));
                schedule.setMealname(resultSet.getString("mealname"));
                schedule.setRecipe(resultSet.getString("recipe"));
                list.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            list = new ArrayList<>();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) MysqlDataUtils.closeConnection(connection);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public boolean authenticateUser(String username, String password) {
        // Use try-with-resources for automatic resource management
        Connection connection = MysqlDataUtils.getConnection();
        String query = "SELECT count(*) FROM users WHERE username = ? AND password = ?";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;  // Return true if user exists
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            if (e.getErrorCode() == 1049) {
                System.out.println("Database does not exist");
            } else {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null) MysqlDataUtils.closeConnection(connection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;  // Return false if user not found or error occurs
    }
}


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}