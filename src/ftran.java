import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class ftran implements Runnable{
    private static int sbal;
    private static int rbal;
    public Socket client;
    private String rbank;
    private String session_id;
    private String receiver;
    private int amount;
    private String sender;
    private String tr[];
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String receiver_mail;
    private String query1;
    private String status;
    private String query2;
    private boolean tup = true;
    public ftran(Socket client, String session_id, String receiver_mail, String rbank){
        this.client = client;
        this.session_id = session_id;
        this.receiver_mail = receiver_mail;
        this.rbank = rbank;
    }
    @Override
    public void run(){
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String password = "root";
        System.out.println("session timer started...");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            System.out.println("5 minutes is up! session ended");
            scheduler.shutdown();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.MINUTES
            );
        try{
            dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            System.out.println(session_id);
            String check_mail = "SELECT * FROM active WHERE sid='"+session_id+"'";
            ResultSet resultSet = statement.executeQuery(check_mail);
            String sid = null;
            String fname = null;
            String lname =null;
            String mail = null;
            int sbalance = 0;
            int loan = 0;
            System.out.println("fetching sender mail from DB...");
            while(resultSet.next()){
                mail = resultSet.getString("mail");
            }
            System.out.println(mail);
            resultSet.close();
            System.out.println("fetching sender info from DB...");
            String fetch = "SELECT * FROM users WHERE mail='"+mail+"'";
            ResultSet resultSet2 = statement.executeQuery(fetch);
            System.out.println("done");
            while (resultSet2.next()){
                sid = resultSet2.getString("id");
                fname = resultSet2.getString("fname");
                lname = resultSet2.getString("lname");
                sbalance = resultSet2.getInt("balance");
                loan = resultSet2.getInt("loan");
            }
            System.out.println("done");
            resultSet2.close();
            tr=dataInputStream.readUTF().split("&");
            System.out.println("receiver: "+tr[0]);
            receiver = tr[0];
            amount = Integer.parseInt(tr[1]);
            sender = tr[2];
            System.out.println(sender);
            String query = "SELECT * FROM users WHERE mail='"+receiver+"'";
            String rfname = null;
            String rlname =null;
            String rmail = null;
            int rbalance = 0;
            System.out.println("fetching receiver info from DB...");
            ResultSet resultSet3 = statement.executeQuery(query);
            while(resultSet3.next()){
                rfname = resultSet3.getString("fname");
                rlname = resultSet3.getString("lname");
                rmail = resultSet3.getString("mail");
                rbalance = resultSet3.getInt("balance");
            }
            resultSet3.close();
            if (amount <= sbalance){
                sbal = sbalance - amount;
                rbal = rbalance + amount;
                String upquer = "UPDATE users SET balance="+sbal+" WHERE mail='"+mail+"'";
                String upquer2 = "UPDATE users SET balance="+rbal+" WHERE mail='"+receiver+"'";
                System.out.println(amount+"&"+sbalance);
                try{
                    System.out.println("Updating users table");
                    statement.executeUpdate(upquer);
                    statement.executeUpdate(upquer2);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            try{
                status = "successful";
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                int length = 10;
                char[] randomString = new char[length];
                Random random = new Random();
                for (int i = 0; i < length; i++) {
                    int randomIndex = random.nextInt(characters.length());
                    randomString[i] = characters.charAt(randomIndex);
                }
                String tid = new String(randomString);
                String amt = Integer.toString(amount);
                System.out.println(mail +"&"+ rmail+"&"+ status +"&"+amt + "&"+timestamp+"&"+tid);
                dataOutputStream.writeUTF(mail +"&"+ rmail+"&"+ status +"&"+amt + "&"+timestamp+"&"+tid);
                String update_transaction = "INSERT INTO Transactions (tid, Date, Sender, Receiver, Rbank, Amount) VALUES ('"+tid+"', '"+timestamp+"', '"+mail+"', '"+rmail+"', '"+rbank+"', "+amount+")";
                System.out.println("Updating transactions table");
                statement2.executeUpdate(update_transaction);
                connection.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
