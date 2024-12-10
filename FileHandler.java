import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FileHandler {
    private static final String FILE_PATH = "passwords.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String ENCRYPTION_KEY = "HawkDiddlyRizzGod12345678901234567"; //constants use all caps
    
    private SecretKeySpec secretKey;
    
    public FileHandler() {
        createFileIfNotExists(FILE_PATH);
        createFileIfNotExists(USERS_FILE);
        byte[] keyBytes = new byte[32];
        byte[] originalBytes = ENCRYPTION_KEY.getBytes();
        System.arraycopy(originalBytes, 0, keyBytes, 0, //
            Math.min(originalBytes.length, keyBytes.length));
        secretKey = new SecretKeySpec(keyBytes, "AES"); //imports make AES encryption lightwork no reaction
    }
    
    private String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return data; // fallback on error
        }
    }
    
    private String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedData;
        }
    }
    
    private void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void savePasswords(Map<String, List<PasswordRecord>> categoryMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (List<PasswordRecord> records : categoryMap.values()) {
                for (PasswordRecord record : records) {
                    String line = String.format("%s|%s|%s|%s",
                        record.getAccountName(),
                        record.getUsername(),
                        record.getPassword(),
                        record.getCategory());
                    writer.write(encrypt(line) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String, List<PasswordRecord>> loadPasswords() {
        Map<String, List<PasswordRecord>> categoryMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                try {
                    String decrypted = decrypt(line);
                    String[] parts = decrypted.split("\\|");
                    if (parts.length >= 4) {
                        PasswordRecord record = new PasswordRecord(
                            parts[0], parts[1], parts[2], parts[3]);
                        categoryMap
                            .computeIfAbsent(parts[3], k -> new ArrayList<>())
                            .add(record);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categoryMap;
    }
    public boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) { //got to reuse buff and file from old projects
            // format -> username|passwordHash|firstName|lastName|hint
            writer.write(String.format("%s|%s|%s|%s|%s%n",
                user.getUsername(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getPasswordHint()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getPasswordHint(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(username)) {
                    return parts[4];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getUsersFilePath() {
        return USERS_FILE;
    }
} 