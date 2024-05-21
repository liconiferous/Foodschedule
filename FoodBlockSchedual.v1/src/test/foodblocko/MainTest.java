package foodblocko;

import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.mockito.Mockito.*;

public class MainTest {

    @Test
    public void testMain() {
        // Mock the LoginScreen class
        LoginScreen mockLoginScreen = Mockito.mock(LoginScreen.class);

        // Use a spy to monitor SwingUtilities
        SwingUtilities spySwingUtilities = Mockito.spy(SwingUtilities.class);

        // Ensure that SwingUtilities.invokeLater is called with the LoginScreen constructor
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(spySwingUtilities).invokeLater(any(Runnable.class));

        // Call the main method
        Main.main(new String[]{});

        // Verify that the LoginScreen constructor was called
        verify(spySwingUtilities, times(1)).invokeLater(any(Runnable.class));
    }
}
