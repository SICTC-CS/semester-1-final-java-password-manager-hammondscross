public class PasswordRecord {
    private String accountName;
    private String username;
    private String password;
    private String category;
    
    public PasswordRecord(String accountName, String username, String password, String category) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
        this.category = category;
    }
    
    // Getters
    public String getAccountName() { return accountName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getCategory() { return category; }
} 