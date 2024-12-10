import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static PasswordManager manager = new PasswordManager();
    
    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            if (!login()) {
                System.out.println("Goodbye!");
                break;
            }
            
            while (running) {
                displayMenu();
                int choice = getMenuChoice();
                
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
                        validatePassword();
                        break;
                    case 5:
                        deleteAccount();
                        break;
                    case 6:
                        modifyAccount();
                        break;
                    case 7:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        
        scanner.close();
    }
    
    private static boolean login() {
        System.out.println("Welcome to Password Manager!");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.print("Choose an option: ");
        
        int choice = getMenuChoice();
        switch (choice) {
            case 1:
                return loginExistingUser();
            case 2:
                return createNewUser();
            default:
                System.out.println("Invalid choice. Please try again.");
                return false;
        }
    }
    
    private static boolean loginExistingUser() {
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            if (manager.login(username, password)) {
                return true;
            }
            
            attempts--;
            if (attempts > 0) {
                System.out.println("Invalid credentials. " + attempts + " attempts remaining.");
                System.out.print("Would you like to see your password hint? (y/n): ");
                if (scanner.nextLine().toLowerCase().startsWith("y")) {
                    manager.showPasswordHint(username);
                }
            }
        }
        System.out.println("Too many failed attempts. Program will now exit.");
        return false;
    }
    
    private static boolean createNewUser() {
        System.out.println("\nCreate New Account");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        String password;
        do {
            System.out.print("Password (min 8 chars, 1 uppercase, 1 number, 1 special char): ");
            password = scanner.nextLine();
            if (!PasswordValidator.isValid(password)) {
                System.out.println("Password does not meet requirements. Please try again.");
            }
        } while (!PasswordValidator.isValid(password));
        
        System.out.print("Password Hint: ");
        String hint = scanner.nextLine();
        
        if (manager.createUser(username, password, firstName, lastName, hint)) {
            System.out.println("Account created successfully!");
            return manager.login(username, password);
        } else {
            System.out.println("Username already exists. Please try again.");
            return false;
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n=== Password Manager Menu ===");
        System.out.println("1. Add Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Generate Password");
        System.out.println("4. Validate Password");
        System.out.println("5. Delete Account");
        System.out.println("6. Modify Account");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }
    
    private static int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void addAccount() {
        System.out.print("Account Name: ");
        String accountName = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        
        manager.addAccount(accountName, username, password, category);
        System.out.println("Account added successfully!");
    }
    
    private static void viewAccounts() {
        System.out.println("\n=== Categories ===");
        for (String category : manager.getCategories()) {
            System.out.println("\nCategory: " + category);
            System.out.println("---------------------------------");
            
            for (PasswordRecord record : manager.getAccountsByCategory(category)) {
                System.out.println("Account: " + record.getAccountName());
                System.out.println("  - Username: " + record.getUsername());
                System.out.println("  - Password: " + record.getPassword());
                System.out.println("---------------------------------");
            }
        }
    }
    
    private static void generatePassword() {
        System.out.print("Enter desired password length (minimum 8): ");
        try {
            int length = Integer.parseInt(scanner.nextLine());
            String password = PasswordGenerator.generatePassword(length);
            System.out.println("Generated Password: " + password);
            
            if (PasswordValidator.isValid(password)) {
                System.out.println("Password meets all requirements!");
                double crackTime = PasswordValidator.calculateCrackTime(password);
                System.out.println("Estimated time to crack: " + 
                    PasswordValidator.getReadableCrackTime(crackTime));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default length of 12");
            String password = PasswordGenerator.generatePassword(12);
            System.out.println("Generated Password: " + password);
        }
    }
    
    private static void validatePassword() {
        System.out.print("Enter password to validate: ");
        String password = scanner.nextLine();
        
        if (PasswordValidator.isValid(password)) {
            System.out.println("Password meets all requirements!");
            double crackTime = PasswordValidator.calculateCrackTime(password);
            System.out.println("Estimated time to crack: " + 
                PasswordValidator.getReadableCrackTime(crackTime));
        } else {
            System.out.println("Password does not meet requirements:");
            System.out.println("- Minimum 8 characters");
            System.out.println("- At least 1 uppercase letter");
            System.out.println("- At least 1 number");
            System.out.println("- At least 1 special character (!@#$%^&*()_+-=[]{}|;:,.<>?)");
        }
    }
    
    private static void deleteAccount() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== Delete Account ===");
        System.out.println("Available categories:");
        for (String category : manager.getCategories()) {
            System.out.println("- " + category);
        }
        
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        
        List<PasswordRecord> accounts = manager.getAccountsByCategory(category);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found in this category.");
            return;
        }
        
        System.out.println("\nAccounts in " + category + ":");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("%d. %s (Username: %s)%n", 
                i + 1, 
                accounts.get(i).getAccountName(),
                accounts.get(i).getUsername());
        }
        
        System.out.print("Enter account number to delete (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("Deletion cancelled.");
                return;
            }
            if (choice < 1 || choice > accounts.size()) {
                System.out.println("Invalid account number.");
                return;
            }
            
            if (manager.deleteAccount(category, choice - 1)) {
                System.out.println("Account deleted successfully!");
            } else {
                System.out.println("Failed to delete account.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void modifyAccount() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== Modify Account ===");
        System.out.println("Available categories:");
        for (String category : manager.getCategories()) {
            System.out.println("- " + category);
        }
        
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        
        List<PasswordRecord> accounts = manager.getAccountsByCategory(category);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found in this category.");
            return;
        }
        
        System.out.println("\nAccounts in " + category + ":");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("%d. %s (Username: %s)%n", 
                i + 1, 
                accounts.get(i).getAccountName(),
                accounts.get(i).getUsername());
        }
        
        System.out.print("Enter account number to modify (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("Modification cancelled.");
                return;
            }
            if (choice < 1 || choice > accounts.size()) {
                System.out.println("Invalid account number.");
                return;
            }
            
            System.out.println("\nWhat would you like to modify?");
            System.out.println("1. Account Name");
            System.out.println("2. Username");
            System.out.println("3. Password");
            System.out.println("4. Category");
            System.out.print("Choose an option: ");
            
            int fieldChoice = Integer.parseInt(scanner.nextLine());
            String newValue;
            
            switch (fieldChoice) {
                case 1:
                    System.out.print("Enter new account name: ");
                    newValue = scanner.nextLine();
                    if (manager.modifyAccount(category, choice - 1, "accountName", newValue)) {
                        System.out.println("Account name updated successfully!");
                    }
                    break;
                case 2:
                    System.out.print("Enter new username: ");
                    newValue = scanner.nextLine();
                    if (manager.modifyAccount(category, choice - 1, "username", newValue)) {
                        System.out.println("Username updated successfully!");
                    }
                    break;
                case 3:
                    System.out.print("Enter new password: ");
                    newValue = scanner.nextLine();
                    if (manager.modifyAccount(category, choice - 1, "password", newValue)) {
                        System.out.println("Password updated successfully!");
                    }
                    break;
                case 4:
                    System.out.print("Enter new category: ");
                    newValue = scanner.nextLine();
                    if (manager.modifyAccount(category, choice - 1, "category", newValue)) {
                        System.out.println("Category updated successfully!");
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
} 