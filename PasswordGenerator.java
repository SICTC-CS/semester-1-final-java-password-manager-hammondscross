import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    public static String generatePassword(int length) {
        if (length < 8) length = 8; // min length
        
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // at least one of each required character type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        
        // fill rest randomly
        String allChars = LOWERCASE + UPPERCASE + NUMBERS + SPECIAL;
        while (password.length() < length) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // shuffles password
        return shuffleString(password.toString());
    }
    
    private static String shuffleString(String input) {
        List<Character> characters = input.chars()
            .mapToObj(ch -> (char)ch)
            .collect(Collectors.toList());
        Collections.shuffle(characters);
        return characters.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
} 