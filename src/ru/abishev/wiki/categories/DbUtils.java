package ru.abishev.wiki.categories;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbUtils {
    private static Map<String, Connection> filePathToConnection = new HashMap<String, Connection>();

    private static Connection _getConnectionForDbFile(File dbFile) {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.
                    getConnection("jdbc:h2:" + dbFile.getAbsolutePath(), "sa", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnectionForDbFile(File dbFile) {
        Connection connection = filePathToConnection.get(dbFile.getAbsolutePath());
        try {
            if (connection != null && connection.isValid(10)) {
                return connection;
            }
        } catch (SQLException e) {
        }
        connection = _getConnectionForDbFile(dbFile);
        filePathToConnection.put(dbFile.getAbsolutePath(), connection);
        return connection;
    }

    public static void executeSql(String sql, File dbFile) throws SQLException {
        Connection conn = getConnectionForDbFile(dbFile);
        conn.createStatement().execute(sql);
    }

    public static <T> List<T> executeQuerySql(String sql, String columnName, Class<T> elementClazz, File dbFile) throws SQLException {
        Connection conn = getConnectionForDbFile(dbFile);
        ResultSet _result = conn.createStatement().executeQuery(sql);
        List<T> result = new ArrayList<T>();
        while (_result.next()) {
            result.add((T) _result.getObject(columnName));
        }
        return result;
    }

    public static List<String> executeQuerySqlString(String sql, String columnName, File dbFile) throws SQLException {
        Connection conn = getConnectionForDbFile(dbFile);
        ResultSet _result = conn.createStatement().executeQuery(sql);
        List<String> result = new ArrayList<String>();
        while (_result.next()) {
            result.add(_result.getString(columnName));
        }
        return result;
    }
}
