import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf; 

public class Main {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (Exception ex) {
                    System.err.println("Failed to initialize LaF");
                }
                new LoginPage().setVisible(true);
            }
        });
    }
}