# Contact Manager

A modern desktop contact management application built with Java Swing and styled using FlatLaf. This application provides an intuitive graphical interface for creating, viewing, updating, and deleting contacts with built-in input validation.

---

## Features

- **User Authentication**: Secure login interface to restrict application access.
- **Add Contacts**: Easily add new contact records with name, phone number, and email address.
- **Update Contacts**: Modify existing contact details directly from the interactive contact table.
- **Delete Contacts**: Remove selected contact entries with a single click.
- **View Contacts**: Display all stored contacts in a clean, scrollable tabular view.
- **Input Validation**: Enforces input constraints (non-empty fields, phone numbers up to 15 digits, and valid Gmail formatting).
- **Modern UI Styling**: Custom gradient header panels and clean FlatLaf Light Look-and-Feel styling.

---

## Technologies Used

- **Java** (JDK 21+)
- **Java Swing** (Desktop GUI Framework)
- **FlatLaf** (Look and Feel Theme - `flatlaf-3.4.1.jar`)
- **Java AWT** (Layout Management & Graphics Rendering)
- **Java Collections Framework** (`ArrayList` for in-memory data management)

> **Note:** Data is currently managed in-memory during application runtime. No external database (such as MySQL) is required to run this project.

---

## Project Structure

```text
Contact-Manager/
│
├── src/
│   ├── Main.java            # Main entry point (initializes FlatLaf & launches LoginPage)
│   ├── LoginPage.java       # User authentication window
│   ├── MainFrame.java       # Core application window (table view & CRUD forms)
│   ├── Contact.java         # Data model representing a contact entity
│   └── BackgroundPanel.java # Custom JPanel rendering gradient background aesthetics
│
├── lib/
│   └── flatlaf-3.4.1.jar   # FlatLaf Look-and-Feel dependency library
│
├── assets/
│   └── icon.png            # Application logo / header icon
│
├── .gitignore               # Git exclusion rules for compiled binaries and IDE configs
└── README.md                # Project documentation
```

---

## Requirements

- **Java Development Kit (JDK)**: Java 21 or higher (OpenJDK / Oracle JDK)
- **Operating System**: Windows, macOS, or Linux

---

## How to Run

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/Contact-Manager.git
cd Contact-Manager
```

### 2. Default Login Credentials
Use the following credentials to log in:
- **Username:** `shravan`
- **Password:** `pass123`

### 3. Compile and Run via Command Line

#### On Windows (PowerShell / Command Prompt):
```powershell
# Create bin directory for compiled binaries
mkdir bin -ErrorAction SilentlyContinue

# Compile all source files with FlatLaf classpath
javac -cp "lib/flatlaf-3.4.1.jar" -d bin src/*.java

# Launch application
java -cp "bin;lib/flatlaf-3.4.1.jar" Main
```

#### On Linux / macOS (Terminal):
```bash
# Create bin directory for compiled binaries
mkdir -p bin

# Compile all source files with FlatLaf classpath
javac -cp "lib/flatlaf-3.4.1.jar" -d bin src/*.java

# Launch application
java -cp "bin:lib/flatlaf-3.4.1.jar" Main
```

---

## Screenshots

*(Screenshots can be added here once generated)*

| Login Page | Main Contact Dashboard |
|:---:|:---:|
| `![Login Screen](assets/screenshots/login.png)` | `![Main Dashboard](assets/screenshots/dashboard.png)` |

---

## Future Improvements

The following capabilities are planned as future enhancements:

- **Database Persistence**: Integration with MySQL / JDBC or SQLite for persistent contact storage across application restarts.
- **Search & Filter**: Real-time search bar to quickly filter contacts by name, phone number, or email.
- **Import / Export**: Capability to import and export contacts in CSV or VCF format.
- **Enhanced Authentication**: Multi-user support with hashed credentials and dynamic user registration.

---

## License

This project is open-source and available under the [MIT License](LICENSE).
