
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("Choose your action: ");
            System.out.println("1. Create a file;");
            System.out.println("2. Delete a file;");
            System.out.println("3. Read a file;");
            System.out.println("4. Append to a file;");
            System.out.println("5. Quit the program.");
            int action = in.nextInt();
            switch (action) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    isRunning = false;
                    break;
                default:
                    System.out.println("The program did not understand your input.");
                    break;
            }
        }
    }
}
