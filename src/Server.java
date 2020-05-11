import CommandProcessing.Commands;
import ReceivingConnections.Receiver;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String args[]) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        Statement statement = connection.createStatement();
        String sqlNewTable = "CREATE TABLE users " +
                "(login VARCHAR(50), " +
                "password VARCHAR(50));";
        String sqlCommand = "CREATE TABLE spacemarine " +
                "(id  SERIAL PRIMARY KEY NOT NULL, " +
                "creationDate DATE, " +
                "name VARCHAR(50), " +
                "height INTEGER, " +
                "health INTEGER, " +
                "weapon VARCHAR(50), " +
                "meleeWeapon VARCHAR(50), " +
                "x FLOAT, " +
                "y FLOAT, " +
                "chapter_name VARCHAR(50), " +
                "chapter_marinesCount BIGINT, " +
                "login VARCHAR(50), " +
                "password VARCHAR(50));";
        String delete = "DROP TABLE spacemarine;";
        String delete1 = "DROP TABLE users;";
     //   statement.executeUpdate(sqlNewTable);
      //  statement.executeUpdate(delete);
       // statement.executeUpdate(sqlCommand);
        Commands commands = new Commands();
        commands.uploadData();
        new Receiver();
    }
}
