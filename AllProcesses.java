package Project2;

import java.io.*;
import java.util.ArrayList;
import java.net.Socket;

public class AllProcesses implements Runnable{
    public static ArrayList<AllProcesses> allProcesses = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;
    public AllProcesses(Socket socket) { // Constructor
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //gets the client name
            this.clientName = bufferedReader.readLine();

            //Adds process to ArrayList
            allProcesses.add(this);
            messageToAll("SERVER: " + clientName + " has connected.");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        //message from client
        String sentMessage;
        while (socket.isConnected()) {
            try {
                //program will halt until we receive message from client
                sentMessage = bufferedReader.readLine();
                messageToAll(sentMessage);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void messageToAll(String sentMessage) {

        if (sentMessage.equals("Stop")) {
            messageToAll("SERVER: Stopping all threads.");
            for (AllProcesses allProcesses : AllProcesses.allProcesses) {
                allProcesses.closeAll(allProcesses.socket, allProcesses.bufferedReader, allProcesses.bufferedWriter);
            }
            return;
        }

        if (sentMessage.startsWith("SERVER: ")) {
            for (AllProcesses allProcesses : AllProcesses.allProcesses) {
                try {
                    if (!allProcesses.clientName.equals(clientName)) {
                        allProcesses.bufferedWriter.write(sentMessage);
                        allProcesses.bufferedWriter.newLine();
                        allProcesses.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            return;
        }
        //split array holds message string
        String[] split = sentMessage.split(" ", 4);

        if (!split[2].equals("0")) {
            for (AllProcesses allProcesses : AllProcesses.allProcesses) {
                try {
                    //message sent as whisper to one client
                    if (!allProcesses.clientName.equals(clientName) && allProcesses.clientName.contains(split[2])) {
                        allProcesses.bufferedWriter.write(split[0] + " " + split[3]);
                        allProcesses.bufferedWriter.newLine();
                        allProcesses.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            return;
        }
        //message to all clients
        for (AllProcesses allProcesses : AllProcesses.allProcesses) {
            try {
                if (!allProcesses.clientName.equals(clientName)) {
                    allProcesses.bufferedWriter.write(split[0] + " " + split[3]);
                    allProcesses.bufferedWriter.newLine();
                    allProcesses.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }

    }


    public void removeClient() { //disconnects single process
        allProcesses.remove(this);
        messageToAll("SERVER: " + clientName + " has disconnected.");
    }
    private void closeAll(Socket s, BufferedReader br, BufferedWriter bw) {
        removeClient();
        try {
            // closes BufferedReader
            if (br != null)
                br.close();
            // closes BufferedWriter
            if (bw != null)
                bw.close();
            // closes the Socket
            if (s != null)
                s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}