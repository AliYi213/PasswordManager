import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

public class PasswordManager {

    private static final String PASSWORD = "passwords.txt";
    private static final String KEY = "aes.key";
    private static SecretKey secretKey;
    private static final String ALGORITHM = "AES";

    static void initKey() throws Exception {
        if (Files.exists(Paths.get(KEY))) {
            byte[] keyBytes = Files.readAllBytes(Paths.get(KEY));
            secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        } else {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256, new SecureRandom());
            secretKey = keyGen.generateKey();
            Files.write(Paths.get(KEY), secretKey.getEncoded());
        }
    }

    static void createFile(String fileName) throws IOException {
        new File(fileName).createNewFile();
    }

    static void encryptFile() throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(PASSWORD));
        int blockSize = 16;
        int length = fileData.length;
        if (length % blockSize != 0) {
            int newSize = length + blockSize - length % blockSize;
            byte[] newData = new byte[newSize];
            System.arraycopy(fileData, 0, newData, 0, length);
            fileData = newData;
        }
        byte[] encryptedData = encrypt(fileData);
        Files.write(Paths.get(PASSWORD), encryptedData);
    }

    static void decryptFile() throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(PASSWORD));
        byte[] decryptedData = decrypt(fileData);
        Files.write(Paths.get(PASSWORD), decryptedData);
    }

    private static byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    static void save(Scanner scanner) throws IOException {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Application: ");
        String application = scanner.nextLine();
        System.out.print("Other information: ");
        String otherInfo = scanner.nextLine();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD, true))) {
            writer.write(title + "," + password + "," + application + "," + otherInfo);
            writer.newLine();
        }
    }

    static void search(Scanner scanner) throws IOException {
        System.out.print("Enter title to search: ");
        String title = scanner.nextLine();
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(title + ",")) {
                    System.out.println("Password found: " + line);
                    return;
                }
            }
        }
        System.out.println("Password not found.");
    }

    static void update(Scanner scanner) throws IOException {
        System.out.print("Enter title to update: ");
        String title = scanner.nextLine();
        List<String> lines = Files.readAllLines(Paths.get(PASSWORD));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD))) {
            boolean updated = false;
            for (String line : lines) {
                if (line.startsWith(title + ",")) {
                    System.out.print("New Password: ");
                    String newPassword = scanner.nextLine();
                    line = title + "," + newPassword + "," + line.split(",")[2] + "," + line.split(",")[3];
                    updated = true;
                }
                writer.write(line);
                writer.newLine();
            }
            if (!updated) {
                System.out.println("Password not found.");
            }
        }
    }
    static void delete(Scanner scanner) throws IOException {
        System.out.print("Enter title to delete: ");
        String title = scanner.nextLine();
        List<String> lines = Files.readAllLines(Paths.get(PASSWORD));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD))) {
            boolean deleted = false;
            for (String line : lines) {
                if (!line.startsWith(title + ",")) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    deleted = true;
                }
            }
            if (!deleted) {
                System.out.println("Password not found.");
            }
        }
    }
    static void displayMenu() {
        System.out.println("1. Save Password");
        System.out.println("2. Search Password");
        System.out.println("3. Update Password");
        System.out.println("4. Delete Password");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }
}
