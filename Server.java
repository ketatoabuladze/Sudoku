import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
 
public class Server{

    private static final Sudoku sudoku = new Sudoku();
    private static final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    private static volatile boolean isRunning = true;
    public static void main(String[] args) throws IOException {
         sudoku.fillValues();

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        
         
        int portNumber = Integer.parseInt(args[0]);
        ServerSocket serverSocket =
        new ServerSocket(Integer.parseInt(args[0]));


        ExecutorService executor = Executors.newCachedThreadPool();

        while(isRunning){
            try{
                 Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket));
            } catch (IOException e){
                if (!isRunning) break;
                System.out.println("Connection error: " + e.getMessage());
            }
        }

        executor.shutdown();
        serverSocket.close();
        System.out.println("Server shut down");
         
    }


    private static synchronized boolean makeMove(int row, int col, int value) {
        if(sudoku.enterNumber(row, col, value)){
            sudoku.R--;
            return true;
        }
        return false;  
    }
 
    private static synchronized void broadcast(String message) {
        for (PrintWriter writer : clients.values()){
            writer.println(message);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }


        @Override
        public void run() {

            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String clientName = "Player" + socket.getPort();
                clients.put(clientName, out);

                out.println("Welcome, Use 'UPDATE row col value' to make a move");
                out.println(sudoku.getSudokuString());
                broadcast( clientName + "joined");
                
                String input;

                while((input = in.readLine()) != null) {
                    if (!isRunning) break;

                    if(input.startsWith("UPDATE")) {
                        handleUpdate(clientName, input);
                    }else {
                        out.println("unkown command");
                    }

                    if(input.startsWith("show")){
                        broadcast(sudoku.getSudokuString());
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection error" + e.getMessage());
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(socket.getPort());
                broadcast("A Player left the game");
            }

        }

        private void handleUpdate(String clientName, String input){
            try{
                String[] parts = input.split(" ");
                if (parts.length != 4) {
                    out.println("invalid input format");
                    return;
                }

                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                int value = Integer.parseInt(parts[3]);

                if(makeMove(row, col, value)){
                    broadcast(clientName + "updated the board");
                    broadcast(sudoku.getSudokuString());

                    if(sudoku.isBoardFull()){
                        broadcast("the board is full shutting down server");  
                        isRunning = false;
                    }
                }else {
                    out.println("invalid move");
                }

            } catch(NumberFormatException e){
                out.println("Error: Invalid input, must use integers");

            }
        }

    }
}

