
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
            client.listFiles();

            System.out.println("Choose your action:");
            System.out.println("1. Create a file.     Command: 1 [file_name]");
            System.out.println("2. Delete a file.     Command: 2 [file_name]");
            System.out.println("3. Read a file.       Command: 3 [file_name]");
            System.out.println("4. Append to a file.  Command: 4 [file_name] [line]");
            System.out.println("5. Quit the program.  Command: 5");
            System.out.println("Enter your command:");
            String operation = in.nextLine();

            String[] myArgs = operation.split("\\s+");

            if (myArgs[0].equals("exit")) {
                break;
            } else {
                try {
                    int action = Integer.parseInt(myArgs[0]);
                    String fileName = null;
                    switch (action) {
                        case 1:
                            if (myArgs.length >= 2) {
                                fileName = myArgs[1];
                                client.createFile(rootPath + fileName);
                            }
                            break;
                        case 2:
                            if (myArgs.length >= 2) {
                                fileName = myArgs[1];
                                client.deleteFile(rootPath + fileName);
                            }
                            break;
                        case 3:
                            if (myArgs.length >= 2) {
                                fileName = myArgs[1];
                                client.readFile(rootPath + fileName);
                            }
                            break;
                        case 4:
                            if (myArgs.length >= 3) {
                                fileName = myArgs[1];
                                if (!client.fileExists(rootPath + fileName)) {
                                    break;
                                }
                                StringBuilder line = new StringBuilder();
                                for (int i = 2; i < myArgs.length; i++) {
                                    line.append(myArgs[i] + " ");
                                }
                                client.appendToFile(rootPath + fileName, line.toString().trim());
                            }
                            break;
                        case 5:
                            isRunning = false;
                            break;
                        default:
                            System.out.println("The program did not understand your input.");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please, enter a number.");
                    continue;
                }
                System.out.println();
            }
        }

        in.close();
    }
}
