import java.util.*;
import java.io.*;
import java.security.SecureRandom;

public class PasswordManager {
    private static final String DATA_FILE = "passwords.txt";
    private Map<String, List<Account>> categoryAccounts;
    private Scanner scanner;
    private User currentUser;
    
    public PasswordManager() {
        categoryAccounts = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean isAuthenticated = authenticate();
        if (!isAuthenticated) {
            System.out.println("Authentication failed. Exiting program.");
            return;
        }

        loadData();
        showMainMenu();
    }

    private boolean authenticate() {
        int attempts = 0;
        while (attempts < 3) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (choice == 1) {
                if (login()) return true;
            } else if (choice == 2) {
                if (register()) return login();
            }
            
            attempts++;
            System.out.println("Attempts remaining: " + (3 - attempts));
        }
        return false;
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== Password Manager Menu ===");
            System.out.println("1. Add Account");
            System.out.println("2. View Accounts by Category");
            System.out.println("3. Generate Password");
            System.out.println("4. Modify Account");
            System.out.println("5. Delete Account");
            System.out.println("6. Exit");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    addAccount();
                    break;
                case 2:
                    viewAccounts();
                    break;
                case 3:
                    generatePassword();
                    break;
                case 4:
                    modifyAccount();
                    break;
                case 5:
                    deleteAccount();
                    break;
                case 6:
                    saveData();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void addAccount() {
        System.out.print("Enter account name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password (or type 'generate' for auto-generation): ");
        String password = scanner.nextLine();
        
        if (password.equalsIgnoreCase("generate")) {
            password = PasswordGenerator.generate();
        } else {
            while (!PasswordValidator.isValid(password)) {
                System.out.println("Password does not meet requirements. Please try again: ");
                password = scanner.nextLine();
            }
        }
        
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        
        Account account = new Account(name, username, password, category);
        categoryAccounts.computeIfAbsent(category, k -> new ArrayList<>()).add(account);
        
        System.out.println("Account added successfully!");
    }

    private void viewAccounts() {
        if (categoryAccounts.isEmpty()) {
            System.out.println("No accounts stored yet.");
            return;
        }

        System.out.println("\nAvailable Categories:");
        for (String category : categoryAccounts.keySet()) {
            System.out.println("- " + category);
        }

        System.out.print("\nEnter category to view: ");
        String category = scanner.nextLine();

        List<Account> accounts = categoryAccounts.get(category);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found in this category.");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println("\nAccount " + (i + 1) + ":");
            System.out.println("  - Username: " + acc.getUsername());
            System.out.println("  - Password: " + acc.getPassword());
            System.out.println("---------------------------------");
        }
    }

    private boolean login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    currentUser = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data.");
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    private boolean register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter password hint: ");
        String hint = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(String.format("%s,%s,%s,%s,%s%n", 
                username, password, firstName, lastName, hint));
            return true;
        } catch (IOException e) {
            System.out.println("Error registering user.");
            return false;
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Account account = new Account(parts[0], parts[1], parts[2], parts[3]);
                    categoryAccounts.computeIfAbsent(parts[3], k -> new ArrayList<>()).add(account);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing password data found.");
        }
    }

    private void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (List<Account> accounts : categoryAccounts.values()) {
                for (Account account : accounts) {
                    writer.write(String.format("%s,%s,%s,%s%n",
                        account.name,
                        account.getUsername(),
                        account.getPassword(),
                        account.category));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving password data.");
        }
    }

    private void modifyAccount() {
        if (categoryAccounts.isEmpty()) {
            System.out.println("No accounts to modify.");
            return;
        }

        viewAccounts();
        System.out.print("Enter category of account to modify: ");
        String category = scanner.nextLine();
        
        List<Account> accounts = categoryAccounts.get(category);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("Category not found or empty.");
            return;
        }

        System.out.print("Enter account number to modify: ");
        int accountNum = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (accountNum < 1 || accountNum > accounts.size()) {
            System.out.println("Invalid account number.");
            return;
        }

        Account account = accounts.get(accountNum - 1);
        System.out.println("1. Modify username");
        System.out.println("2. Modify password");
        System.out.print("Choose option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter new username: ");
                account.username = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter new password (or 'generate'): ");
                String newPassword = scanner.nextLine();
                if (newPassword.equalsIgnoreCase("generate")) {
                    account.password = PasswordGenerator.generate();
                } else {
                    while (!PasswordValidator.isValid(newPassword)) {
                        System.out.println("Password does not meet requirements. Try again: ");
                        newPassword = scanner.nextLine();
                    }
                    account.password = newPassword;
                }
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        System.out.println("Account modified successfully!");
    }

    private void deleteAccount() {
        if (categoryAccounts.isEmpty()) {
            System.out.println("No accounts to delete.");
            return;
        }

        viewAccounts();
        System.out.print("Enter category of account to delete: ");
        String category = scanner.nextLine();
        
        List<Account> accounts = categoryAccounts.get(category);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("Category not found or empty.");
            return;
        }

        System.out.print("Enter account number to delete: ");
        int accountNum = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (accountNum < 1 || accountNum > accounts.size()) {
            System.out.println("Invalid account number.");
            return;
        }

        accounts.remove(accountNum - 1);
        if (accounts.isEmpty()) {
            categoryAccounts.remove(category);
        }
        System.out.println("Account deleted successfully!");
    }

    private void generatePassword() {
        String password = PasswordGenerator.generate();
        System.out.println("Generated password: " + password);
    }

    // Other necessary classes

    static class Account {
        private String name;
        private String username;
        private String password;
        private String category;

        public Account(String name, String username, String password, String category) {
            this.name = name;
            this.username = username;
            this.password = password;
            this.category = category;
        }

        // Getters and setters
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    static class User {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String hint;

        public User(String username, String password, String firstName, String lastName, String hint) {
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.hint = hint;
        }
    }

    static class PasswordGenerator {
        private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        private static final SecureRandom random = new SecureRandom();

        public static String generate() {
            StringBuilder password = new StringBuilder();
            // Ensure at least one of each required character type
            password.append(CHARS.charAt(random.nextInt(26))); // capital letter
            password.append(CHARS.charAt(26 + random.nextInt(26))); // lowercase letter
            password.append(CHARS.charAt(52 + random.nextInt(10))); // number
            password.append(CHARS.charAt(62 + random.nextInt(8))); // special char

            // Fill rest of password
            while (password.length() < 12) {
                password.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }

            // Shuffle the password
            char[] passwordArray = password.toString().toCharArray();
            for (int i = passwordArray.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                char temp = passwordArray[i];
                passwordArray[i] = passwordArray[j];
                passwordArray[j] = temp;
            }

            return new String(passwordArray);
        }
    }

    static class PasswordValidator {
        public static boolean isValid(String password) {
            if (password.length() < 8) return false;
            if (!password.matches(".*[A-Z].*")) return false;
            if (!password.matches(".*[0-9].*")) return false;
            if (!password.matches(".*[!@#$%^&*].*")) return false;
            return true;
        }
    }
}
