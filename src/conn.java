import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class conn {
    private static String ip;
    private static int port;
    private static String inf;
    private static String[] minf;
    private static String status;
    private static String session_id;
    private static String type;
    public static void main(String[] args) throws Exception {
        ip = "172.20.144.1";
        port = 3333;
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("Server is running on :" + localhost.getHostAddress()+":"+port);
        InetAddress addr = InetAddress.getByName(localhost.getHostAddress());
        ServerSocket serverSocket = new ServerSocket(port, 50, addr);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client ip:" +socket.getInetAddress());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            minf = dataInputStream.readUTF().split("&");
            status = minf[0];
            session_id = minf[1];
            type = minf[2];
            if (status.equals("old")){
                System.out.println("Client is old");
                switch (type){
                    case "tran":
                        System.out.println("Type: Transfer");
                        transfer transfer = new transfer(socket, session_id);
                        Thread thread = new Thread(transfer);
                        thread.start();
                }
            }else{
            user_information uinf = new user_information(socket);
            Thread thread = new Thread(uinf);
            thread.start();
            }
        }
    }
}
