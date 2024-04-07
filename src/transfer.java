import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
public class transfer implements Runnable{
    private Socket client;
    private String mail;
    private String passw;
    private String sid;
    private String rmail;
    private String rb;
    private Connection connection;
    private Statement statement;
    public transfer(Socket client, String sid){
        this.client = client;
        this.sid = sid;
    }
    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String password = "root";
        String fdata = null;
        try {
            System.out.println("starting...");
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            String[] rec = dataInputStream.readUTF().split("&");
            String bname = rec[0];
            rmail = rec[1];
            System.out.println(bname);
            System.out.println(rmail);
            String query1 = "SELECT * FROM users WHERE mail='"+rmail+"'";
            ResultSet resultSet = statement.executeQuery(query1);
            String fname = null;
            String lname = null;
            switch (bname){
                case "bank":
                String rbname = "bank";
                System.out.println("bcase");
                    while(resultSet.next()){
                        fname = resultSet.getString("fname");
                        lname = resultSet.getString("lname");
                        System.out.println(fname+" "+lname);
                        dataOutputStream.writeUTF(fname+"&"+lname);
                        ftran ftran = new ftran(client, sid, passw, rbname);
                        Thread thread = new Thread(ftran);
                        thread.start();
                        break;
                    }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
