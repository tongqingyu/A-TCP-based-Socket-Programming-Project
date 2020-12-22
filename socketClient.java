import java.io.*;
import java.net.*;
import java.util.Scanner;

public class socketClient {
    private String hostname;
    private int port;
    private String userName;

    public socketClient(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public class ReadThread extends Thread {
        private BufferedReader reader;
        private Socket socket;
        private socketClient client;

        public ReadThread(Socket socket, socketClient client){
            this.socket = socket;
            this.client = client;

            try{
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));

            }catch(IOException ex){
                System.out.println("Error getting input stream: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void run(){
            while(true){
                try{
                    String response = reader.readLine();
                    System.out.println("\n" + response);

                    if(client.getUserName()!=null){
                        System.out.print("[" + client.getUserName() + "]: ");
                    }
                } catch(IOException ex){
                    System.out.println("Error reading from server" + ex.getMessage());
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }
    public class WriteThread extends Thread {
        private PrintWriter writer;
        private Socket socket;
        private socketClient client;

        public WriteThread(Socket socket, socketClient client) {
            this.socket = socket;
            this.client = client;

            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (IOException ex) {
                System.out.println("Error getting output stream: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void run() {

            Scanner s = new Scanner(System.in);
            String userName = s.nextLine();
            client.setUserName(userName);
            writer.println(userName);


            String text;

            do {
                text = s.nextLine();
                writer.println(text);

            } while (!text.equals("bye"));

            try {
                socket.close();
            } catch (IOException ex) {

                System.out.println("Error writing to server: " + ex.getMessage());
            }
        }
    }

    public void execute(){
        try{
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected !");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        }catch(UnknownHostException ex){
            System.out.println("Server not found" + ex.getMessage());
        }catch(IOException ex){
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    void setUserName(String userName){
        this.userName = userName;
    }
    String getUserName(){
        return this.userName;
    }

    public static void main(String[] args){

        //When communicate within one computer
        //String hostname = "localhost" ;
        //When the server is running on anther computer, hostname is  server side computer's IP address
        String hostname = "100.64.9.115";

        int port = 8000;

        socketClient client = new socketClient(hostname, port);
        client.execute();
    }
}
