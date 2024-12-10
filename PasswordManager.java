import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PasswordManager {
    private User currentUser;
    private Map<String, List<PasswordRecord>> categoryMap;
    private FileHandler fileHandler;
    
    public PasswordManager() {
        this.fileHandler = new FileHandler();
        this.categoryMap = fileHandler.loadPasswords();
    }
    
    public boolean login(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileHandler.getUsersFilePath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && 
                    parts[0].equals(username) && 
                    parts[1].equals(String.valueOf(password.hashCode()))) {
                    currentUser = new User(parts[0], password, parts[2], parts[3], parts[4]);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void addAccount(String accountName, String username, 
                          String password, String category) {
        PasswordRecord record = new PasswordRecord(accountName, username, 
                                                 password, category);
        categoryMap
            .computeIfAbsent(category, k -> new ArrayList<>())
            .add(record);
        fileHandler.savePasswords(categoryMap);
    }
    
    public List<String> getCategories() {
        return new ArrayList<>(categoryMap.keySet());
    }
    
    public List<PasswordRecord> getAccountsByCategory(String category) {
        return categoryMap.getOrDefault(category, new ArrayList<>());
    }
    
    public boolean createUser(String username, String password, String firstName, String lastName, String hint) {
        // Check if username already exists
        if (fileHandler.userExists(username)) {
            return false;
        }
        
        User newUser = new User(username, password, firstName, lastName, hint);
        fileHandler.saveUser(newUser);
        currentUser = newUser;
        return true;
    }
    
    public void showPasswordHint(String username) {
        String hint = fileHandler.getPasswordHint(username);
        if (hint != null) {
            System.out.println("Password Hint: " + hint);
        } else {
            System.out.println("No hint found for this username, maybe the account doesn't exist?");
        }
    }
    
    public boolean deleteAccount(String category, int index) {
        List<PasswordRecord> accounts = categoryMap.get(category);
        if (accounts == null || index < 0 || index >= accounts.size()) {
            return false;
        }
        
        accounts.remove(index);
        if (accounts.isEmpty()) {
            categoryMap.remove(category);
        }
        
        fileHandler.savePasswords(categoryMap);
        return true;
    }
    
    public boolean modifyAccount(String category, int index, String field, String newValue) {
        List<PasswordRecord> accounts = categoryMap.get(category);
        if (accounts == null || index < 0 || index >= accounts.size()) {
            return false;
        }
        
        PasswordRecord oldRecord = accounts.get(index);
        PasswordRecord newRecord;
        
        switch (field) {
            case "accountName":
                newRecord = new PasswordRecord(newValue, oldRecord.getUsername(), 
                    oldRecord.getPassword(), oldRecord.getCategory());
                break;
            case "username":
                newRecord = new PasswordRecord(oldRecord.getAccountName(), newValue, 
                    oldRecord.getPassword(), oldRecord.getCategory());
                break;
            case "password":
                newRecord = new PasswordRecord(oldRecord.getAccountName(), oldRecord.getUsername(), 
                    newValue, oldRecord.getCategory());
                break;
            case "category":
                newRecord = new PasswordRecord(oldRecord.getAccountName(), oldRecord.getUsername(), 
                    oldRecord.getPassword(), newValue);
                accounts.remove(index);
                if (accounts.isEmpty()) {
                    categoryMap.remove(category);
                }
                categoryMap.computeIfAbsent(newValue, k -> new ArrayList<>()).add(newRecord);
                fileHandler.savePasswords(categoryMap);
                return true;
            default:
                return false;
        }
        
        if (field != "category") {
            accounts.set(index, newRecord);
        }
        fileHandler.savePasswords(categoryMap);
        return true;
    }
} 