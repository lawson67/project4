import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    //"\\\\C:\\Users\\Nezza\\workspace\\Projects\\bin\\the_file_name"
    private ChatFilter c = new ChatFilter("badwords.txt");
    //private ChatFilter c = new ChatFilter("\\\\C:\\Users\\Nezza\\workspace\\Projects\\bin\\the_file_name");
    
    private ChatServer(int port) {
        this.port = port;
    }
    

    private void broadcast(String message){
        //TO DO: MAKE CONCURRENT! METHOD IS SHARED ACROSS ALL CLIENTS


        //The method will then print the message to the terminal of every client.
        //This will be done by iterating through the client list and writing the message using the
        //      writeMessage(String msg) method.
        //You will also need to print the message to the server’s terminal with a simple print statement.
        
        String pattern = "HH:mm:ss";
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        String currTime = timeFormat.format(new Date());
        ((ArrayList<ClientThread>)clients).trimToSize();
        for (int i = 0; i < clients.size(); i++) {
                clients.get(i).writeMessage(message);
        }
        //System.out.println(currTime + " Server Broadcast: " + message);


    }
    
    private void directMessage(String message, String username){
        String pattern = "HH:mm:ss";
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        String currTime = timeFormat.format(new Date());
        ((ArrayList<ClientThread>)clients).trimToSize();
        for (int i = 0; i < clients.size(); i++) {
        	//System.out.println(clients.get(i).username);
        	//System.out.println(username);
        	if (clients.get(i).username.equals(username)){
                clients.get(i).writeMessage(message);
        	}
        }
    }

    private void remove(int id){
        //TO DO: MAKE CONCURRENT HERE. METHOD IS SHARED ACROSS EVERY CLIENT THREAD.


        //The remove method will take an integer as input that is used to determine which client
        //      to remove from the clients ArrayList.
        ((ArrayList<ClientThread>)clients).trimToSize();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id == id){
                clients.remove(i);
            }
        }
    }

    private void close(){
        //This method does the exact same as logging out in the ChatClient class.
        ((ArrayList<ClientThread>)clients).trimToSize();
        for (int i = 0; i < clients.size(); i++) {
            try {
                //TERMINATION MESSAGE TO CLOSE A CLIENT.
                clients.get(i).sOutput.writeObject("Server has closed the connection");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0); //TEMPORARY
    }


    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) { //infinite while loop implemented to keep server running.

                //Checks if a ClientThread is disconnected and remove if so
                ((ArrayList<ClientThread>)clients).trimToSize();
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).isSocketConnected() == false){
                        clients.remove(i);
                        System.out.println("ClientThread removed");
                    }
                }

                //TO DO: accept console / server commands from server chat.
                // Current issue: if scanner is before t.start(), client is never seen until user inputs in server chat.
                //      If scanner is after t.start(), scanner never takes an input.

                /*Scanner scan = new Scanner(System.in);
                if (scan.nextLine().contains("/broadcast")){
                    System.out.println("broadcasting");
                }
                else if (scan.nextLine().equals("/close")){
                    System.out.println("closing");
                }*/

                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();

                //System.out.println("asdadadadadasdads");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        int port = 1500;
        for (int i = 0; i < args.length; i++){
            port = Integer.parseInt(args[i]);
        }
        ChatServer server = new ChatServer(port);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        boolean socketConnected = true;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                socketConnected = false;
            }
        }

        public synchronized boolean isSocketConnected(){
            return socketConnected;
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public synchronized void run() {
            // Read the username sent to you by client
            while(socketConnected){ //implemented to keep checking for user input.
                String pattern = "HH:mm:ss";
                SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
                String currTime = timeFormat.format(new Date());

                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //Handles what type of ChatMessage and acts respectively to the type.
                if (cm.getType() == 1){
                    System.out.println(currTime + " " + username + " disconnected with a LOGOUT message.");
                    try {
                        //TERMINATION MESSAGE TO CLOSE A CLIENT.
                        sOutput.writeObject("Server has closed the connection");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socketConnected = false;
                }
                else if (cm.getType() == 0){
                    // Print message to server chat / console
                	String msge = cm.getMessage();
                	msge = c.filter(msge);
                	//System.out.println(currTime + " " + username + ": " + msge);
                    // Send message back to the client
                    
                    broadcast(currTime + " " + username + ": " + msge + "\n");
                    /*try {
                        sOutput.writeObject(currTime + " " + username + ": " + msge + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                } else if (cm.getType() == 2){
                	//System.out.println("2");
                	String msge = cm.getMessage();
                	msge = c.filter(msge);
                	//System.out.println(cm.getRecipient());
                	directMessage(currTime + " " + username + " -> " + cm.getRecipient() + ": " + msge + "\n", cm.getRecipient());
                }
            }

            //System.out.println("closed");
        }

        private synchronized boolean writeMessage(String msg){
            //writeMessage will return false if the socket is not connected and true otherwise.
            //Before returning true, make sure you actually write the message to the ClientThread’s
            //      ObjectOutputStream using the writeObject method from the ObjectOutputStream class.

            boolean isConnected = true;
            System.out.println(msg);
            try {
                sOutput.writeObject(msg);
            } 
            catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
            }
            return isConnected;
        }

    }
}
