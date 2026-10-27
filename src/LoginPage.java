import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Login window for authentication into Contact Manager.
 */
public class LoginPage extends JFrame {

    public LoginPage() {
        setTitle("Contact Manager - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(null); 
        
        try {
            String logoPath = Files.exists(Paths.get("assets", "logo.png")) ? "assets/logo.png" : (Files.exists(Paths.get("assets", "icon.png")) ? "assets/icon.png" : "logo.png");
            ImageIcon logoIcon = new ImageIcon(new ImageIcon(logoPath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBounds(150, 30, 100, 100);
            mainPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Logo image not found.");
        }

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 160, 80, 25);
        userLabel.setForeground(Color.WHITE);
        mainPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(130, 160, 180, 25);
        mainPanel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 210, 80, 25);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(130, 210, 180, 25);
        mainPanel.add(passwordText);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 270, 100, 30);
        mainPanel.add(loginButton);

        JButton signUpButton = new JButton("Don't have an account? Sign Up");
        signUpButton.setBounds(80, 315, 240, 25);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(signUpButton);

        signUpButton.addActionListener(_ -> {
            dispose();
            new SignUpPage().setVisible(true);
        });

        add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText().trim();
                String password = new String(passwordText.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username and Password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int userId = DatabaseManager.authenticateUser(username, password);
                if (userId != -1) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    dispose(); 
                    new MainFrame(userId).setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
