import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class ftran implements Runnable{
    private int fbal;
    private int rbal;
    private Socket client;
    private String sid;
    private String receiver;
    private int amount;
    private String sender;
    private String tr[];
    private DataInputStream dataInputStream;
    private String rec;
    private String query1;
    private String query2;
    private boolean tup = false;
    public ftran(Socket client, String sid, String rec){
        this.client = client;
        this.sid = sid;
        this.rec = rec;
    }
    @Override
    public void run(){
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String password = "root";
        tup = false;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            System.out.println("5 minutes is up! session ended");
            tup = true;
            scheduler.shutdown();
            System.exit(0);
        }, 10, TimeUnit.MINUTES
            );
        try{
            dataInputStream = new DataInputStream(client.getInputStream());
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String query1 = "SELECT * FROM active WHERE sid='"+sid+"'";
            ResultSet resultSet = statement.executeQuery(query1);
            boolean found = false;
            String sid = null;
            String fname = null;
            String lname =null;
            int age = 0;
            String mail = null;
            String passw = null;
            int balance = 0;
            int loan = 0;
            while(resultSet.next()){
                found = true;
                sid = resultSet.getString("id");
                fname = resultSet.getString("fname");
                lname = resultSet.getString("lname");
                mail = resultSet.getString("mail");
                passw = resultSet.getString("passw");
                balance = resultSet.getInt("balance");
                loan = resultSet.getInt("loan");
            }
            while(!tup){
                tr=dataInputStream.readUTF().split("&");
                tup = true;
            //query1 = "SELECT value1, value2, (value1 + value2) AS sum FROM numbers";
            }
            receiver = tr[0];
            amount = Integer.parseInt(tr[1]);
            sender = tr[2];
            String rsid = null;
            String rfname = null;
            String rlname =null;
            int rage = 0;
            String rmail = null;
            String rpassw = null;
            int rbalance = 0;
            int rloan = 0;
            while(resultSet.next()){
                found = true;
                rsid = resultSet.getString("id");
                rfname = resultSet.getString("fname");
                rlname = resultSet.getString("lname");
                rmail = resultSet.getString("mail");
                rpassw = resultSet.getString("passw");
                rbalance = resultSet.getInt("balance");
                rloan = resultSet.getInt("loan");
            }
            if (amount <= balance){
                fbal = balance - amount;
                rbal = rbalance + amount;
            }
            String upquer = "UPDATE users SET balance="+fbal+"";
            try{
                statement.executeUpdate(upquer);
            }catch(Exception e){
                e.printStackTrace();
            }
            String upquer2 = "UPDATE users SET balance="+rbal+" WHERE mail='"+receiver+"'";
            try{
                statement.executeUpdate(upquer2);
            }catch(Exception e){
                e.printStackTrace();
            }
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
