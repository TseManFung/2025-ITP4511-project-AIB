package AIB.db;

import java.sql.SQLException;
import java.io.IOException;

public class resetDB {
    public static void main(String[] args) {
        // Update these variables with your database credentials
        String url = "jdbc:mysql://192.9.149.106:3306/ITP4511_DB?useSSL=false&allowMultiQueries=true";
        String user = "ITP4511user";
        String password = "P@$$w0rd";

        ITP4511_DB db = new ITP4511_DB(url, user, password);
        try {
            db.createDB();
            System.out.println("Database reset successfully.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
