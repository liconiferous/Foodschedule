package foodblocko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoginScreenTest {

    private LoginScreen loginScreen;
    private DatabaseConnection mockDB;

    @BeforeEach
    public void setUp() {
        mockDB = mock(DatabaseConnection.class);
        loginScreen = new LoginScreen();
        loginScreen.DB = mockDB;
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        String testUsername = "testUser";
        String testPassword = "testPass";

        loginScreen.usernameField.setText(testUsername);
        loginScreen.passwordField.setText(testPassword);

        when(mockDB.authenticateUser(testUsername, testPassword)).thenReturn(true);

        // Act
        loginScreen.actionPerformed(new ActionEvent(loginScreen.loginButton, ActionEvent.ACTION_PERFORMED, "Login"));

        // Assert
        verify(mockDB).authenticateUser(testUsername, testPassword);
        assertEquals("", loginScreen.usernameField.getText());
        assertEquals("", loginScreen.passwordField.getText());
    }

    @Test
    public void testLoginFailure() {
        // Arrange
        String testUsername = "wrongUser";
        String testPassword = "wrongPass";

        loginScreen.usernameField.setText(testUsername);
        loginScreen.passwordField.setText(testPassword);

        when(mockDB.authenticateUser(testUsername, testPassword)).thenReturn(false);

        // Act
        loginScreen.actionPerformed(new ActionEvent(loginScreen.loginButton, ActionEvent.ACTION_PERFORMED, "Login"));

        // Assert
        verify(mockDB).authenticateUser(testUsername, testPassword);
        assertEquals(testUsername, loginScreen.usernameField.getText());
        assertEquals(testPassword, new String(loginScreen.passwordField.getPassword()));
    }

    @Test
    public void testRegistrationSuccess() {
        // Arrange
        String newUsername = "newUser";
        String newPassword = "newPass";
        String confirmPassword = "newPass";
        String email = "newEmail@example.com";

        loginScreen.newUsernameField.setText(newUsername);
        loginScreen.newPasswordField.setText(newPassword);
        loginScreen.confirmPasswordField.setText(confirmPassword);
        loginScreen.newEmailField.setText(email);

        // Act
        loginScreen.actionPerformed(new ActionEvent(loginScreen.confirmRegistrationButton, ActionEvent.ACTION_PERFORMED, "Register"));

        // Assert
        verify(mockDB).insertUser(newUsername, newPassword, email);
        assertEquals("", loginScreen.newUsernameField.getText());
        assertEquals("", new String(loginScreen.newPasswordField.getPassword()));
        assertEquals("", new String(loginScreen.confirmPasswordField.getPassword()));
        assertEquals("", loginScreen.newEmailField.getText());
    }

    @Test
    public void testRegistrationPasswordMismatch() {
        // Arrange
        String newUsername = "newUser";
        String newPassword = "newPass";
        String confirmPassword = "differentPass";
        String email = "newEmail@example.com";

        loginScreen.newUsernameField.setText(newUsername);
        loginScreen.newPasswordField.setText(newPassword);
        loginScreen.confirmPasswordField.setText(confirmPassword);
        loginScreen.newEmailField.setText(email);

        // Act
        loginScreen.actionPerformed(new ActionEvent(loginScreen.confirmRegistrationButton, ActionEvent.ACTION_PERFORMED, "Register"));

        // Assert
        verify(mockDB, never()).insertUser(anyString(), anyString(), anyString());
        assertEquals(newUsername, loginScreen.newUsernameField.getText());
        assertEquals(newPassword, new String(loginScreen.newPasswordField.getPassword()));
        assertEquals(confirmPassword, new String(loginScreen.confirmPasswordField.getPassword()));
        assertEquals(email, loginScreen.newEmailField.getText());
    }
}
