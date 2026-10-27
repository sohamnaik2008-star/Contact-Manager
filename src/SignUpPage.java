import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Registration window for new users in Contact Manager.
 */
public class SignUpPage extends JFrame {

    public SignUpPage() {
        setTitle("Contact Manager - Sign Up");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(null);

        try {
            String logoPath = Files.exists(Paths.get("assets", "logo.png")) ? "assets/logo.png" : (Files.exists(Paths.get("assets", "icon.png")) ? "assets/icon.png" : "logo.png");
            ImageIcon logoIcon = new ImageIcon(new ImageIcon(logoPath).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBounds(160, 20, 80, 80);
            mainPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Logo image not found.");
        }

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(35, 125, 110, 25);
        userLabel.setForeground(Color.WHITE);
        mainPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(150, 125, 195, 25);
        mainPanel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(35, 175, 110, 25);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(150, 175, 195, 25);
        mainPanel.add(passwordText);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setBounds(35, 225, 115, 25);
        confirmLabel.setForeground(Color.WHITE);
        mainPanel.add(confirmLabel);

        JPasswordField confirmText = new JPasswordField(20);
        confirmText.setBounds(150, 225, 195, 25);
        mainPanel.add(confirmText);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(140, 285, 120, 30);
        mainPanel.add(signUpButton);

        JButton backButton = new JButton("Back to Login");
        backButton.setBounds(140, 330, 120, 30);
        mainPanel.add(backButton);

        add(mainPanel);

        signUpButton.addActionListener(_ -> {
            String username = userText.getText().trim();
            String password = new String(passwordText.getPassword());
            String confirmPassword = new String(confirmText.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.length() < 3) {
                JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.usernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = DatabaseManager.registerUser(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                dispose();
                new LoginPage().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error creating account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(_ -> {
            dispose();
            new LoginPage().setVisible(true);
        });
    }
}
