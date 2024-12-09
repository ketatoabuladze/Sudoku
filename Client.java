import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Client <server_address> <port_number>");
            System.exit(1);
        }

        String serverAddress = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket socket = new Socket(serverAddress, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the server at " + serverAddress + ":" + portNumber);

            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from the server.");
                }
            });

            listenerThread.start();

            while (true) {
                System.out.println("Enter command (show or update i j num): ");
                String userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("show")) {
                    out.println("show");
                } else if (userInput.startsWith("update")) {
                    String[] parts = userInput.split(" ");
                    if (parts.length == 4) {
                        try {
                            int row = Integer.parseInt(parts[1]);
                            int col = Integer.parseInt(parts[2]);
                            int num = Integer.parseInt(parts[3]);

                            out.println("UPDATE " + row + " " + col + " " + num);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Row, column, and value must be integers.");
                        }
                    } else {
                        System.out.println("Invalid update command. Use: update <row> <col> <num>");
                    }
                } else if (userInput.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnecting from the server...");
                    break;
                } else {
                    System.out.println("Unknown command. Use 'show', 'update i j num', or 'exit'.");
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverAddress);
        } catch (IOException e) {
            System.err.println("I/O error while connecting to the server: " + e.getMessage());
        }
    }
}
