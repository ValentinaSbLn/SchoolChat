
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private final int DEFAULT_PORT = 8585;
    private static final String DEFAULT_HOST = "localhost";

    public Client() {
        try {
            socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void run() {
        try {
            ChatClient user = new ChatClient(in, out);
            user.run();
        } catch (Exception ex) {
            close();
        }
    }


    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
