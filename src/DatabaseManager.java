import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:contacts.db";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
        initDatabase();
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password_hash TEXT NOT NULL, "
                + "salt TEXT NOT NULL"
                + ");";

        String createContactsTable = "CREATE TABLE IF NOT EXISTS contacts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "name TEXT NOT NULL, "
                + "phone TEXT NOT NULL, "
                + "email TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createContactsTable);

            seedSampleData();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void seedSampleData() {
        boolean isEmpty = false;
        String countSql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                isEmpty = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user count: " + e.getMessage());
            return;
        }

        if (!isEmpty) {
            return;
        }

        if (registerUser("demo", "demo123")) {
            int demoUserId = authenticateUser("demo", "demo123");
            if (demoUserId != -1) {
                String[][] sampleContacts = {
                    {"Aarav Sharma", "9876543210", "aarav.sharma@gmail.com"},
                    {"Priya Patel", "9823456781", "priya.patel@gmail.com"},
                    {"Rohan Mehta", "9988776655", "rohan.mehta@gmail.com"},
                    {"Sneha Iyer", "9765432109", "sneha.iyer@gmail.com"},
                    {"Karan Singh", "9123456780", "karan.singh@gmail.com"},
                    {"Ananya Desai", "9898989898", "ananya.desai@gmail.com"},
                    {"Vikram Joshi", "9012345678", ""},
                    {"Neha Kulkarni", "9345678123", "neha.kulkarni@gmail.com"},
                    {"Aditya Nair", "9567891234", "aditya.nair@gmail.com"},
                    {"Isha Gupta", "9700112233", ""}
                };

                String insertSql = "INSERT INTO contacts (user_id, name, phone, email) VALUES (?, ?, ?, ?)";
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String[] contact : sampleContacts) {
                        pstmt.setInt(1, demoUserId);
                        pstmt.setString(2, contact[0]);
                        pstmt.setString(3, contact[1]);
                        pstmt.setString(4, contact[2]);
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.err.println("Error inserting sample contacts: " + e.getMessage());
                }
            }
        }
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean registerUser(String username, String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        if (hash == null || salt == null) {
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hash);
            pstmt.setString(3, salt);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static int authenticateUser(String username, String password) {
        String sql = "SELECT id, password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String storedHash = rs.getString("password_hash");
                    String storedSalt = rs.getString("salt");

                    if (verifyPassword(password, storedHash, storedSalt)) {
                        return userId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return -1;
    }

    public static List<Contact> getContacts(int userId) {
        List<Contact> contacts = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        loadUserContacts(userId, contacts, ids);
        return contacts;
    }

    public static List<Integer> getContactIds(int userId) {
        List<Contact> contacts = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        loadUserContacts(userId, contacts, ids);
        return ids;
    }

    public static void loadUserContacts(int userId, List<Contact> contacts, List<Integer> ids) {
        contacts.clear();
        ids.clear();
        String sql = "SELECT id, name, phone, email FROM contacts WHERE user_id = ? ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                    contacts.add(new Contact(
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email") != null ? rs.getString("email") : ""
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading contacts: " + e.getMessage());
        }
    }

    public static int addContact(int userId, Contact contact) {
        String sql = "INSERT INTO contacts (user_id, name, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, contact.getName());
            pstmt.setString(3, contact.getPhone());
            pstmt.setString(4, contact.getEmail());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
        return -1;
    }

    public static boolean updateContact(int contactId, Contact contact) {
        String sql = "UPDATE contacts SET name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setInt(4, contactId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteContact(int contactId) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting contact: " + e.getMessage());
            return false;
        }
    }

    // --- PBKDF2 Password Hashing Utilities ---

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    private static boolean verifyPassword(String password, String expectedHashBase64, String saltBase64) {
        String computedHash = hashPassword(password, saltBase64);
        if (computedHash == null || expectedHashBase64 == null) {
            return false;
        }
        return java.security.MessageDigest.isEqual(
                Base64.getDecoder().decode(expectedHashBase64),
                Base64.getDecoder().decode(computedHash)
        );
    }
}
