package foodblocko;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;


public class LoginScreen extends JFrame implements ActionListener {
    final JTextField usernameField;
    final JPasswordField passwordField;
    final JTextField newEmailField;
    final JButton loginButton;
    private final JButton registerButton;
    final JTextField newUsernameField;
    final JPasswordField newPasswordField;
    final JPasswordField confirmPasswordField;
    final JButton confirmRegistrationButton;
    private final JPanel cards;
   DatabaseConnection DB = new DatabaseConnection();

    public LoginScreen() {
        setTitle("Food Schedule");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create the login panel
        JPanel loginPanel = new JPanel(new GridLayout(4, 1));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register new account");

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        // Create the registration panel
        JPanel registerPanel = new JPanel(new GridLayout(5, 1));
        JLabel newUsernameLabel = new JLabel("New Username:");
        JLabel newPasswordLabel = new JLabel("New Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel newEmailLabel = new JLabel("Email address: ");
        newUsernameField = new JTextField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        newEmailField = new JTextField(20);
        confirmRegistrationButton = new JButton("Confirm Registration");
        confirmRegistrationButton.addActionListener(this);

        registerPanel.add(newUsernameLabel);
        registerPanel.add(newUsernameField);
        registerPanel.add(newPasswordLabel);
        registerPanel.add(newPasswordField);
        registerPanel.add(confirmPasswordLabel);
        registerPanel.add(confirmPasswordField);
        registerPanel.add(newEmailLabel);
        registerPanel.add(newEmailField);
        registerPanel.add(confirmRegistrationButton);


        // Create the panel that uses CardLayout
        cards = new JPanel(new CardLayout());
        cards.add(loginPanel, "login");
        cards.add(registerPanel, "register");

        // Add the cards panel to the frame
        add(cards);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        if (e.getSource() == loginButton) {
            // Login button clicked
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            boolean isAuthenticated = DB.authenticateUser(username, password);

            if (isAuthenticated) {
                // Here you can add code to authenticate the user
                // For now, let's just print the credentials
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                // Display a success message (optional) and proceed
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.setVisible(false);  // Close the login window
                // After successful login, you can open the main application window
                // For now, let's just close the login window
                ScheduleWindow scheduleWindow = new ScheduleWindow(username);
                scheduleWindow.addHierarchyListener(new HierarchyListener() {
                    @Override
                    public void hierarchyChanged(HierarchyEvent e) {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (!scheduleWindow.isShowing()) {
                                usernameField.setText("");
                                passwordField.setText("");
                                setVisible(true);
                            }
                        }
                    }
                });
                scheduleWindow.setVisible(true);
            } else {
                // Display an error message if authentication fails
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == registerButton) {
            // Switch to the registration view
            newUsernameField.setText("");
            newPasswordField.setText("");
            newEmailField.setText("");
            confirmPasswordField.setText("");
            cardLayout.show(cards, "register");
        } else if (e.getSource() == confirmRegistrationButton) {
            // Confirm registration button clicked
            String newUsername = newUsernameField.getText();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = newEmailField.getText();
            if (newUsername.trim().length() > 0 && newPassword.trim().length() > 0) {
                if (newPassword.equals(confirmPassword)) {
                    DB.insertUser(newUsername, newPassword, email);
                    System.out.println("New Username: " + newUsername);
                    System.out.println("New Password: " + newPassword);
                    // Show the login panel after registration
                    usernameField.setText("");
                    passwordField.setText("");
                    cardLayout.show(cards, "login");
                } else {
                    JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (newUsername.trim().length() == 0) {
                    JOptionPane.showMessageDialog(this, "请输入用户名", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (newPassword.trim().length() == 0) {
                    JOptionPane.showMessageDialog(this, "请输入密码", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}

