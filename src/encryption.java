import java.sql.*;

public class encryption {
    private String password;

    public boolean check(String mail, String hash) {
        String url = "jdbc:mysql://localhost:3306/clients";
        String username = "root";
        String dbpassword = "root";
        
        try (Connection connection = DriverManager.getConnection(url, username, dbpassword);
             Statement statement = connection.createStatement()) {
            String query = "SELECT passw FROM users WHERE mail='" + mail + "'";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                password = rs.getString("passw");
            }
            return password.equals(hash); // Correctly compare strings
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of SQL exception
        }
    }
}
