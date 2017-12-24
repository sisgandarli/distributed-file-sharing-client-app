
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
            System.out.println("Create a file.     Command: create [file_name]");
            System.out.println("Delete a file.     Command: delete [file_name]");
            System.out.println("Read a file.       Command: read   [file_name]");
            System.out.println("Append to a file.  Command: append [file_name] [line]");
            System.out.println("Quit the program.  Command: exit");
            System.out.println("Enter your command:");
            String operation = in.nextLine();

            String[] myArgs = operation.split("\\s+");

            try {
                String action = myArgs[0];
                String fileName = null;
                switch (action) {
                    case "create":
                        if (myArgs.length >= 2) {
                            fileName = myArgs[1];
                            client.createFile(rootPath + fileName);
                        } else {
                            System.out.println("Your command is not complete.");
                        }
                        break;
                    case "delete":
                        if (myArgs.length >= 2) {
                            fileName = myArgs[1];
                            client.deleteFile(rootPath + fileName);
                        } else {
                            System.out.println("Your command is not complete.");
                        }
                        break;
                    case "read":
                        if (myArgs.length >= 2) {
                            fileName = myArgs[1];
                            client.readFile(rootPath + fileName);
                        } else {
                            System.out.println("Your command is not complete.");
                        }
                        break;
                    case "append":
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
                        } else {
                            System.out.println("Your command is not complete.");
                        }
                        break;
                    case "exit":
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

        in.close();
    }
}
