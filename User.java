public class User {
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String passwordHint;
    
    public User(String username, String password, String firstName, String lastName, String hint) {
        this.username = username;
        this.passwordHash = String.valueOf(password.hashCode());
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHint = hint;
    }
    
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPasswordHint() { return passwordHint; }
} 