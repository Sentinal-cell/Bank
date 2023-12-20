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
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String[] rec = dataInputStream.readUTF().split("&");
            String bname = rec[0];
            String rmail = rec[1];
            String query1 = "SELECT fname, lname FROM users WHERE mail='"+rmail+"'";
            String fname = null;
            String lname = null;
            switch (bname){
                case "bank":
                    ResultSet resultSet = statement.executeQuery(query1);
                    while(resultSet.next()){
                        fname = resultSet.getString("fname");
                        lname = resultSet.getString("lname");
                        dataOutputStream.writeUTF(fname+"&"+lname);
                    }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
