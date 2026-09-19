import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    
    private ArrayList<Contact> contactList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable contactTable;
    private JTextField nameField, phoneField, emailField;

    public MainFrame() {
        setTitle("Contact Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        BackgroundPanel formPanel = new BackgroundPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            String iconPath = java.nio.file.Files.exists(java.nio.file.Paths.get("assets", "icon.png")) ? "assets/icon.png" : "icon.png";
            ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            JLabel iconLabel = new JLabel(icon);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(iconLabel, gbc);
        } catch (Exception e) {
            System.out.println("Icon image not found.");
        }
        
        JLabel titleLabel = new JLabel("Contact Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleLabel, gbc);
        
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(emailField, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"Name", "Phone Number", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        contactTable = new JTable(tableModel);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contactTable), BorderLayout.CENTER);
        
        add(mainPanel);

        addButton.addActionListener(_ -> addContact());
        updateButton.addActionListener(_ -> updateContact());
        deleteButton.addActionListener(_ -> deleteContact());
        clearButton.addActionListener(_ -> clearFields());

        contactTable.getSelectionModel().addListSelectionListener(_ -> {
            if (contactTable.getSelectedRow() != -1) {
                int selectedRow = contactTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                phoneField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Contact contact : contactList) {
            tableModel.addRow(new Object[]{contact.getName(), contact.getPhone(), contact.getEmail()});
        }
    }
    
    private void addContact() {
        try {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Phone cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (phone.length() > 15 || !phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Phone number must be up to 15 digits and contain only numbers.", "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.isEmpty() && !email.toLowerCase().endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Gmail address (e.g., user@gmail.com).", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            contactList.add(new Contact(name, phone, email));
            refreshTable();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding contact: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Phone cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length() > 15 || !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must be up to 15 digits and contain only numbers.", "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.isEmpty() && !email.toLowerCase().endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Gmail address (e.g., user@gmail.com).", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Contact contact = contactList.get(selectedRow);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        refreshTable();
        clearFields();
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        contactList.remove(selectedRow);
        refreshTable();
        clearFields();
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        contactTable.clearSelection();
    }
}