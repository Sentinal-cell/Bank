import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
public class user_information implements Runnable{
    private Socket client;
    private String mail;
    private String passw;
    private boolean state;
    public user_information(Socket client){
        this.client = client;
    }
    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String password = "root";
        String fdata = null;
        try {
            System.out.println("Fetching user data...");
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
            System.out.println("Checking user information...");
            mail = mg[0];
            passw =mg[1];
            encryption encr = new encryption();
            state = encr.check(mail, passw);
            if (state){
                System.out.println("username and password correct...");
                String fetch_query = "SELECT * FROM users WHERE mail = '"+mail+"' AND passw = '"+passw+"'";
                System.out.println("Fetching user data from DB...");
                ResultSet resultSet = statement.executeQuery(fetch_query);
                int id = 0;
                String fname = null;
                String lname =null;
                int age = 0;
                String mail = null;
                String passw = null;
                int balance = 0;
                int loan = 0;
                while(resultSet.next()){
                    System.out.println("user info fetch successfully");
                    id = resultSet.getInt("id");
                    fname = resultSet.getString("fname");
                    lname = resultSet.getString("lname");
                    mail = resultSet.getString("mail");
                    passw = resultSet.getString("passw");
                    balance = resultSet.getInt("balance");
                    loan = resultSet.getInt("loan");
                }
                System.out.println("creating session id...");
                String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                int length = 10;
                char[] randomString = new char[length];
                Random random = new Random();
                for (int i = 0; i < length; i++) {
                    int randomIndex = random.nextInt(characters.length());
                    randomString[i] = characters.charAt(randomIndex);
                }
                String sid = new String(randomString);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String active_update = "INSERT INTO active VALUES ('"+id+"', '"+sid+"', '"+fname+"', '"+lname+"', '"+mail+"', "+balance+", "+loan+", '"+timestamp+"')";
                statement.executeUpdate(active_update);
                System.out.println("active table appended");
                System.out.println("Sending data to client...");
                dataOutputStream.writeUTF("success");
                Thread.sleep(2000);
                dataOutputStream.writeUTF(sid);
                Thread.sleep(2000);
                fdata = sid+"&"+fname+"&"+lname+"&"+mail+"&"+passw+"&"+balance+"&"+loan;
                dataOutputStream.writeUTF(fdata);
                System.out.println("Data sent...");
                client.close();
                connection.close();
                System.out.println("Connection closed...");
            }
            else{
                dataOutputStream.writeUTF("Invalid");
                System.out.println("Client used wrong usrname or password...");
                client.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
