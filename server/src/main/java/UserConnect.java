import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Valentina on 17.11.2016.
 */
class UserConnect extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private final List<String> privateChat;
    private String name;
    private final List<UserConnect> userCon;

    public UserConnect(Socket socket, List<UserConnect> userCon) {
        privateChat = new ArrayList<>();
        this.socket = socket;
        this.userCon = userCon;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    private void userJoin(String name) {
        synchronized (userCon) {
            for (UserConnect user : userCon) {
                (user).out.println("system: " + name + " joined the chat");
            }
        }
    }

    private void userLeft(String name) {
        synchronized (userCon) {
            Iterator<UserConnect> iter = userCon.iterator();
            while (iter.hasNext()) {
                (iter.next()).out.println("system: " + name + " lefted the chat");
            }
        }
    }

    private void handleMessage(String name) throws IOException {
        String str;
        while (true) {
            str = in.readLine();
            if (str.equals("exit")) return;
            if (printAllPrivateMessage(str)) continue;
            sendUserMessage(name, str);
        }
    }

    private void sendUserMessage(String name, String message) {
        synchronized (userCon) {
            Iterator<UserConnect> iter = userCon.iterator();
            while (iter.hasNext()) {

                UserConnect user = iter.next();
                if (!prvtMsg(message, user)) {
                    if (user.name.equals(this.name)) {
                        user.out.println("You: " + message);
                    } else
                        user.out.println(name + ": " + message);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            name = in.readLine();
            userJoin(name);
            handleMessage(name);
            userLeft(name);

        } catch (IOException e) {
            System.out.println("Невозможно прочитать сообщение");
            //   e.printStackTrace();
        } finally {
            userLeft(name);
            close();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();

            userCon.remove(this);

        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private boolean prvtMsg(String message, UserConnect user) {
        boolean isPrivate = false;
        int index = message.lastIndexOf("<<");

        if (index != -1) {
            String privateMessage = message.substring(index + 2, message.length());

            isPrivate = true;
            if (message.substring(0, index).equals(user.name)) {
                user.out.println(name + " private >> " + privateMessage);
                synchronized (user.privateChat) {
                    user.privateChat.add(name + " said >> " + privateMessage);
                }
            }
        }
        return isPrivate;
    }

    private boolean printAllPrivateMessage(String str) {
        if (str.equals("printAll")) {
            out.println("Все сообщения для пользователя: ");
            for (String s : privateChat) {
                out.println(s);
            }
            return true;
        }
        return false;
    }
}
