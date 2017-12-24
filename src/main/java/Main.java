
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {       
        String rootPath = "/";
        
        Scanner in = new Scanner(System.in);
        
        System.out.println("Which server(s) would you like to connect to (separate them by space)?");
        
        String connectionString = "";
        String inputHosts = in.nextLine();
        String[] hosts = inputHosts.split("\\s+");
        for (int i = 0; i < hosts.length; i++) {
            connectionString += hosts[i] + ":2181";
            if (i != hosts.length - 1) {
                connectionString += ",";
            }
        }
        
        Client client = new Client(connectionString);

        boolean isRunning = true;
        while (isRunning) {
            if (!client.isConnected()) {
                client.sendClosedSessionMessage();
            }
            System.out.println("Choose your action: ");
            System.out.println("0. List file names;");
            System.out.println("1. Create a file;");
            System.out.println("2. Delete a file;");
            System.out.println("3. Read a file;");
            System.out.println("4. Append to a file;");
            System.out.println("5. Quit the program.");

            String operation = in.next();
            
            if (operation.equals("exit")) {
                break;
            }
            
            int action = -1;
            try {
                action = Integer.parseInt(operation);
            } catch (Exception e) {
                System.out.println("Please, enter a number.");
                continue;
            }

            String fileName = null;
            switch (action) {
                case 0:
                    client.listFiles();
                    break;
                case 1:
                    System.out.println("Enter fileName: ");
                    fileName = in.next();
                    client.createFile(rootPath + fileName);
                    break;
                case 2:
                    System.out.println("Enter fileName: ");
                    fileName = in.next();
                    client.deleteFile(rootPath + fileName);
                    break;
                case 3:
                    System.out.println("Enter fileName: ");
                    fileName = in.next();
                    client.readFile(rootPath + fileName);
                    break;
                case 4:
                    System.out.println("Enter fileName: ");
                    fileName = in.next();
                    in.nextLine();
                    if (!client.fileExists(rootPath + fileName)) {
                        break;
                    }
                    System.out.println("Enter your text (line): ");
                    String line = in.nextLine();
                    client.appendToFile(rootPath + fileName, line);
                    break;
                case 5:
                    isRunning = false;
                    break;
                default:
                    System.out.println("The program did not understand your input.");
                    break;
            }
            System.out.println();
        }

        in.close();
    }
}
