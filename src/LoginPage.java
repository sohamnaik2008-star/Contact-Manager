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
            String logoPath = Files.exists(Paths.get("assets", "logo.png")) ? "assets/logo.png" : "logo.png";
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

        add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                if (username.equals("shravan") && password.equals("pass123")) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    dispose(); 
                    new MainFrame().setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

