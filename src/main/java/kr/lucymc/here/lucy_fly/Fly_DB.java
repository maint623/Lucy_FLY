package kr.lucymc.here.lucy_fly;
import kr.lucymc.here.lucy_fly.Lucy_FLY;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.sql.*;
import static kr.lucymc.here.lucy_fly.Lucy_FLY.connection;
public class Fly_DB {
    public static void FlyInsert(UUID userid, int F) {
        String sql = "insert into fly (UserID, FlySecond) values ('" + userid +"','" + F +"');";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void FlyUpdate(UUID userid, int F) {
        String sql = "update fly set FlySecond='"+F+"' where UserID='"+userid+"';";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void FlyDel(UUID userid) {
        String sql = "DELETE FROM fly where UserID='"+userid+"';";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Integer FlySelect(UUID userid) {
        String sql = "select * from fly where UserID='"+userid+"';";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            while(true){
                try {
                    if (!Objects.requireNonNull(rs).next()) break;
                    count = rs.getInt("FlySecond");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            rs.close();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isDataExists(String tableName, String columnName, String value) {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = Lucy_FLY.connection;
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, value);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = (count > 0);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return exists;
    }
}