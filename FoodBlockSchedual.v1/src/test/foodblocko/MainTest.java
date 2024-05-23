package foodblocko;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.mockito.Mockito.*;

public class MainTest {

    @Test
    public void testMain() {
        // Mock the LoginScreen class
        LoginScreen mockLoginScreen = Mockito.mock(LoginScreen.class);

        // Create an instance of the Main class
        Main mainInstance = new Main() {
            @Override
            void init() {
                // Mock the Runnable
                Runnable runnable = Mockito.mock(Runnable.class);
                doAnswer(invocation -> {
                    runnable.run();
                    return null;
                }).when(SwingUtilities.class);
                SwingUtilities.invokeLater(any(Runnable.class));
            }
        };

        // Call the main method
        mainInstance.init();

        // Verify that the LoginScreen constructor was called
        verify(mockLoginScreen, times(1));
    }
}
