import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        connectToChat();
        readMessage();
    }

    public void connectToChat() {
        System.out.println("Добро пожаловать в SchoolChat. " +
                "\nОтправить сообщение конкретному пользователю: \"user_name<< текст сообщения\". " +
                "\nПосмотреть все полученные вами приватные сообщения: printAll." +
                "\nВыйти из чата: exit\n");
        enterNameUser();
    }

    private void enterNameUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите свой ник:");
        out.println(scan.nextLine());
    }

    private void readMessage() {
        Receiver receiver = new Receiver();
        receiver.setDaemon(true);
        receiver.start();
        String message = "";
        Scanner scan = new Scanner(System.in);
        while (!message.equals("exit")) {
            message = scan.nextLine();
            out.println(message);
        }
        receiver.stopReceive();
    }

    private class Receiver extends Thread {

        private boolean isRunning = true;

        public void stopReceive() {
            isRunning = false;
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    String messageReceive = in.readLine();
                    System.out.println(messageReceive);
                }
            } catch (SocketException e) {
                System.err.println("Ошибка: связь с сервером прервалась. Программа будет закрыта");
                stopReceive();
                System.exit(0);

            } catch (IOException e) {
                System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }

        }
    }
}
