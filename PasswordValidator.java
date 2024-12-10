public class PasswordValidator {
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    // apparently the average attempts per second for a modern computer
    private static final long ATTEMPTS_PER_SECOND = 1_000_000_000L;
    
    public static boolean isValid(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&  // At least one uppercase
               password.matches(".*[0-9].*") &&  // At least one number
               containsSpecialChar(password);
    }
    
    private static boolean containsSpecialChar(String password) {
        return password.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0);
    }
    
    public static double calculateCrackTime(String password) {
        // possible character set size
        int charSetSize = 0;
        if (password.matches(".*[a-z].*")) charSetSize += 26; // lowercase
        if (password.matches(".*[A-Z].*")) charSetSize += 26; // uppercase
        if (password.matches(".*[0-9].*")) charSetSize += 10; // numbers
        if (containsSpecialChar(password)) charSetSize += SPECIAL_CHARS.length();
        
        // calc total possible combos
        double combinations = Math.pow(charSetSize, password.length());
        
        //checking half of all possibilities
        double averageAttempts = combinations / 2;
        
        // time in seconds
        double seconds = averageAttempts / ATTEMPTS_PER_SECOND;
        
        return seconds;
    }
    
    public static String getReadableCrackTime(double seconds) {
        if (seconds < 60) {
            return String.format("%.1f seconds", seconds);
        } else if (seconds < 3600) {
            return String.format("%.1f minutes", seconds / 60);
        } else if (seconds < 86400) {
            return String.format("%.1f hours", seconds / 3600);
        } else if (seconds < 31536000) {
            return String.format("%.1f days", seconds / 86400);
        } else {
            return String.format("%.1f years", seconds / 31536000);
        }
    }
} 