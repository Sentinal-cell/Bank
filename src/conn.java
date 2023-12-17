import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class conn {
    private static String ip;
    private static int port;
    public static void main(String[] args) throws Exception {
        ip = "172.20.144.1";
        port = 3333;
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("Your local IP address: " + localhost.getHostAddress());
        InetAddress addr = InetAddress.getByName(localhost.getHostAddress());
        ServerSocket serverSocket = new ServerSocket(port, 50, addr);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress());
            info uinf = new info(socket);
            Thread thread = new Thread(uinf);
            thread.start();
        }
    }
}
