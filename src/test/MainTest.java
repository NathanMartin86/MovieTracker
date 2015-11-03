import com.company.Main;
import com.company.Movies;
import com.company.User;
import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by macbookair on 11/3/15.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE movies");
        stmt.execute("DROP TABLE users");
        connection.close();
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Nathan", "");
        User user = Main.selectUser(conn, "Nathan");
        endConnection(conn);
        assertTrue(user != null);
    }
    @Test
    public void testMovie() throws SQLException {
        Connection connection = startConnection();
        Main.insertUser(connection,"Nathan","password");
        Main.insertEntry(connection,1,"Pulp Fiction","Action/Adventure");
        Movies movie = Main.selectEntry(connection,1);
        endConnection(connection);

        assertTrue(movie != null);

    }

    @Test
    public void testMovies() throws SQLException {
        Connection connection = startConnection();
        Main.insertUser(connection,"Nathan","");
        Main.insertUser(connection,"Bob","");
        Main.insertEntry(connection,1,"Forrest Gump","Drama");
        Main.insertEntry(connection,2,"The Matrix","Action/Adventure");
        Main.insertEntry(connection,2,"Dumb and Dumber","Comedy");
        ArrayList<Movies> movies = Main.selectEntries(connection);
        endConnection(connection);

        assertTrue(movies.size()==3);
    }


}

