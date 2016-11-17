import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int DEFAULT_PORT = 8585;
    private final List<UserConnect> userCon = new CopyOnWriteArrayList<>();

    public Server() {
        try (ServerSocket server = new ServerSocket(DEFAULT_PORT)) {
            ExecutorService executorService = Executors.newFixedThreadPool(5);

            while (true) {
                Socket socket = server.accept();
                UserConnect con = new UserConnect(socket, userCon);
                userCon.add(con);
                executorService.execute(con);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {

            synchronized (userCon) {
                for (UserConnect anUserCon : userCon) {
                    (anUserCon).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }


    public static void main(String[] args) throws IOException {
        new Server();
    }
}
