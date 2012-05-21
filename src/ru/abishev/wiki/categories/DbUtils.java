package ru.abishev.wiki.categories;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbUtils {
    public static void executeSql(String sql, File dbFile) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2:" + dbFile.getAbsolutePath(), "sa", "");
        conn.createStatement().execute(sql);
        conn.close();
    }

    public static <T> List<T> executeQuerySql(String sql, String columnName, Class<T> elementClazz, File dbFile) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2:" + dbFile.getAbsolutePath(), "sa", "");
        ResultSet _result = conn.createStatement().executeQuery(sql);
        List<T> result = new ArrayList<T>();
        while (_result.next()) {
            result.add((T) _result.getObject(columnName));
        }
        conn.close();
        return result;
    }
}
