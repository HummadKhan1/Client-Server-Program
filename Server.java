package Project2;

import java.io.*;
import java.net.*;

public class Server {
    //create serverSocket object
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) { // constructor
        this.serverSocket = serverSocket;
    }
    public void startServer() { // starting the chat
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");
                AllProcesses allProcesses = new AllProcesses(socket);
                Thread thread = new Thread(allProcesses);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}