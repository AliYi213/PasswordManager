import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static final String PASSWORD = "passwords.txt";

    public static void main(String[] args) throws Exception {
        PasswordManager passwordManager = new PasswordManager();
        passwordManager.initKey();

        if (Files.exists(Paths.get(PASSWORD))) {
            passwordManager.decryptFile();
        } else {
            passwordManager.createFile(PASSWORD);
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            passwordManager.displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    passwordManager.save(scanner);
                    break;
                case 2:
                    passwordManager.search(scanner);
                    break;
                case 3:
                    passwordManager.update(scanner);
                    break;
                case 4:
                    passwordManager.delete(scanner);
                    break;
                case 5:
                    passwordManager.encryptFile();
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
