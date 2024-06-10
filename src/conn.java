import java.io.DataInputStream;
#start
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class conn {
    private static String ip;
    private static int port;
    private static String inf;
    private static String[] minf;
    private static String status;
    private static String session_id;
    private static String type;
    private static final Logger logger = LogManager.getLogger(conn.class);
    public static void main(String[] args) throws Exception {
        ip = "172.20.144.1";
        port = 3333;
        InetAddress localhost = InetAddress.getLocalHost();
        logger.info("Server is running on : {}:{}", localhost.getHostAddress(), port);
        InetAddress addr = InetAddress.getByName(localhost.getHostAddress());
        ServerSocket serverSocket = new ServerSocket(port, 50, addr);
        while (true) {
            Socket socket = serverSocket.accept();
            logger.info("new connection...");
            logger.info("client ip: {}", socket.getInetAddress());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            minf = dataInputStream.readUTF().split("&");
            status = minf[0];
            session_id = minf[1];
            type = minf[2];
            if (status.equals("old")){
                logger.info("Client is old...");
                switch (type){
                    case "tran":
                        logger.info("Type: Transfer");
                        transfer transfer = new transfer(socket, session_id);
                        Thread thread = new Thread(transfer);
                        thread.start();
                        break;
                    case "alerts":
                        logger.info("Type: Alerts");
                        alert_check alert_check= new alert_check(socket, session_id);
                        Thread thread2 = new Thread(alert_check);
                        thread2.start();
                        break;
                    case "records":
                    System.out.println(type);
                        logger.info("Type: Records");
                        records records = new records(socket, session_id);
                        Thread thread3 = new Thread(records);
                        thread3.start();
                        break;
                }
            }else{
            user_information uinf = new user_information(socket);
            Thread thread = new Thread(uinf);
            thread.start();
            }
        }
    }
}
