import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
public class info implements Runnable{
    private Socket client;
    private String mail;
    private String passw;
    public info(Socket client){
        this.client = client;
    }
    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String password = "root";
        try {
            System.out.println("starting");
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            String jarFilePath = "lib/mysql-connector-j-8.1.0.jar";
            /*URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarFilePath)});
            Class<?> driverClass = classLoader.loadClass("com.mysql.cj.jdbc.Driver");
            java.sql.DriverManager.registerDriver((java.sql.Driver) driverClass.getDeclaredConstructor().newInstance());*/
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String[] mg = dataInputStream.readUTF().split("&");
            System.out.println("stg 1");
            mail = mg[0];
            passw =mg[1];
            System.out.println(mail+" "+ passw);
            String sqlQuery = "SELECT * FROM users WHERE mail = '"+mail+"' AND passw = '"+passw+"'";
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            System.out.println("stg 2");
            boolean found = false;
            int id = 0;
            String fname = null;
            String lname =null;
            int age = 0;
            String mail = null;
            String passw = null;
            int balance = 0;
            int loan = 0;
            System.out.println(resultSet);
            while(resultSet.next()){
                found = true;
                id = resultSet.getInt("id");
                fname = resultSet.getString("fname");
                lname = resultSet.getString("lname");
                mail = resultSet.getString("mail");
                passw = resultSet.getString("passw");
                balance = resultSet.getInt("balance");
                loan = resultSet.getInt("loan");
            }
            System.out.println(fname + lname);
            if(!found){
                dataOutputStream.writeUTF("Invalid");
            } else {
                dataOutputStream.writeUTF("found");
                Thread.sleep(2000);
                dataOutputStream.writeUTF(id+"&"+fname+"&"+lname+"&"+age+"&"+mail+"&"+passw+"&"+balance+"&"+loan);
                System.out.println(id+"&"+fname+"&"+lname+"&"+balance+"&"+loan);
            }
            System.out.println(mail);
            System.out.println(passw);
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
