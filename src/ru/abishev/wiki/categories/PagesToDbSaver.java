package ru.abishev.wiki.categories;

import ru.abishev.Pathes;
import ru.abishev.wiki.parser.AnalyserRunner;
import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static ru.abishev.wiki.categories.DbUtils.executeSql;

public class PagesToDbSaver implements WikiDumpAnalyser {
    private final File db;
    private Connection connection;
    private PreparedStatement statement;

    private Set<Long> ids = new HashSet<Long>();

    public PagesToDbSaver(File outputDbFile) {
        db = outputDbFile;
    }

    @Override
    public void start() throws Exception {
        String sql = "CREATE TABLE TEST(page_id BIGINT PRIMARY KEY, page_title TEXT, page_content TEXT);";

        connection = DbUtils.getConnectionForDbFile(db);
        System.out.println("Sql to execute: " + sql);
        executeSql(sql, db);

        statement = connection.prepareStatement("INSERT INTO TEST values (?, ?, ?)");
    }

    @Override
    public void analysePage(WikiPage page) throws Exception {
        if (ids.contains(page.id)) {
            System.out.println("=(");
            return;
        }
        ids.add(page.id);

//        System.out.println(page);
        statement.setLong(1, page.id);
        statement.setString(2, page.title);
        statement.setString(3, page.text);
        statement.execute();
    }

    @Override
    public void finish() throws Exception {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        AnalyserRunner.analyseSimpleDump(new PagesToDbSaver(Pathes.PAGES_DB), Pathes.WIKI_ARTICLES_SIMPLE_DUMP, Integer.MAX_VALUE, true);
    }
}
