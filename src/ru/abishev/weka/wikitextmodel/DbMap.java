package ru.abishev.weka.wikitextmodel;

import org.jetbrains.annotations.Nullable;
import ru.abishev.wiki.categories.DbUtils;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.abishev.wiki.categories.DbUtils.executeSql;

public class DbMap {
    private final File db;
    private PreparedStatement getStatement, setStatement;

    private final Map<String, String> inner = new HashMap<String, String>();
    private final int memoryCacheSize;

    public DbMap(File dbFile, int memoryCacheSize) {
        db = dbFile;
        this.memoryCacheSize = memoryCacheSize;
    }

    private void checkDb() throws SQLException {
        if (!new File(db.getAbsolutePath() + ".h2.db").exists()) {
            String sql = "CREATE TABLE test(key TEXT, value TEXT);";

            System.out.println("Sql to execute: " + sql);
            executeSql(sql, db);
        }

        if (getStatement == null) {
            getStatement = DbUtils.getConnectionForDbFile(db).prepareStatement("SELECT value FROM test WHERE key = ?");
        }
        if (setStatement == null) {
            setStatement = DbUtils.getConnectionForDbFile(db).prepareStatement("INSERT INTO test VALUES (?, ?)");
        }
    }

    @Nullable
    public String get(String key) {
        if (inner.containsKey(key)) {
            return inner.get(key);
        }

        List<String> result = new ArrayList<String>();
        try {
            checkDb();
            getStatement.setString(1, key);
            ResultSet _result = getStatement.executeQuery();

            while (_result.next()) {
                result.add(_result.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result.size() == 0) {
            return null;
        }

        inner.put(key, result.get(0));

        return result.get(0);
    }

    public void put(String key, String value) {
        try {
            checkDb();
            setStatement.setString(1, key);
            setStatement.setString(2, value);
            setStatement.execute();
            inner.put(key, value);
            if (inner.size() > memoryCacheSize) {
                inner.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DbMap map = new DbMap(new File("./data/db_map_test"), 10);
        System.out.println(map.get("k1"));
        map.put("k1", "d1");
        map.put("k2", "d2");
        System.out.println(map.get("k1"));
        System.out.println(map.get("k2"));
    }
}
