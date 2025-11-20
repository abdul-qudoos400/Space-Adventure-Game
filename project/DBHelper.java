import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {
    // path to Access database file.
    private static final String DB_URL = "jdbc:ucanaccess://leaderboard.accdb";

    // Checks if the player is new by verifying that the username does not already exist in Table1.
    public static boolean isNewPlayer(String username) {
        boolean isNew = true;
        String query = "SELECT COUNT(*) AS count FROM Table1 WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                isNew = false;
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return isNew;
    }

    // This method will update the player's high score if the new score is higher,
    // otherwise inserts a new record if no record exists.
    public static void updatePlayerScore(String username, int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectSql = "SELECT highscore FROM Table1 WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(selectSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int currentScore = rs.getInt("highscore");
                if (score > currentScore) {
                    String updateSql = "UPDATE Table1 SET highscore = ? WHERE username = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                    psUpdate.setInt(1, score);
                    psUpdate.setString(2, username);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                }
            } else {
                String insertSql = "INSERT INTO Table1 (username, highscore) VALUES (?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insertSql);
                psInsert.setString(1, username);
                psInsert.setInt(2, score);
                psInsert.executeUpdate();
                psInsert.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // This method retrieves the leaderboard sorted by highscore in descending order.
    public static ResultSet getLeaderboard() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            String query = "SELECT username, highscore FROM Table1 ORDER BY highscore DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}