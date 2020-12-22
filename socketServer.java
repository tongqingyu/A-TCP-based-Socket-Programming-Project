import java.io.*;
import java.net.*;
import java.util.*;


public class socketServer {
    private int port;
    public socketServer(int port){
        this.port = port;
    }
    // record
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public class UserThread extends Thread {
        private Socket socket;
        private socketServer server;
        private PrintWriter writer;

        public UserThread(Socket socket, socketServer server){
            this.socket = socket;
            this.server = server;
        }

        public void run() {
            try{
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);

                printUsers();

                String userName = reader.readLine();
                server.addUserName(userName);

                String serverMessage = "New user connected: " + userName;
                server.broadcast(serverMessage, this);

                String clientMessage;

                do{
                    clientMessage = reader.readLine();
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.broadcast(serverMessage, this);
                }while(!clientMessage.equals("bye"));

                server.removeUser(userName, this);
                socket.close();

                serverMessage = userName + "quit";
                server.broadcast(serverMessage, this);

            }catch(IOException ex){
                System.out.println("Error in UserThread: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // send a list of online users to the newly connected user
        void printUsers(){
            if(server.hasUsers()){
                writer.println("Connected users: " + server.getUserName());
            }else{
                writer.println("Need to wait..");
            }
        }
        void sendMessage(String message) {

            writer.println(message);
        }
    }

    public void execute() {
        try(ServerSocket serverSocket = new ServerSocket(port)){

            System.out.println("Chat Server is listening on port " + port);

            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        }catch(IOException ex){
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // broadcasting
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    // store username of the newly connected client
    void addUserName(String userName) {
        userNames.add(userName);
    }

    // when a client is disconnected, remove the associated username and userthread
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    // default
    Set<String> getUserName(){
        return this.userNames;
    }

    // return true if there are other users connected
    boolean hasUsers(){
        return !this.userNames.isEmpty();
    }


    public static void main(String[] args){

        int port = 8000;
        socketServer server = new socketServer(port);
        server.execute();
    }
}