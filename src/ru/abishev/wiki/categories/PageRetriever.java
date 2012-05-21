package ru.abishev.wiki.categories;

import ru.abishev.Pathes;

import java.io.File;
import java.sql.SQLException;

public class PageRetriever {
    private final File db;

    public PageRetriever(File db) {
        this.db = db;
    }

    public String getContent(long id) throws SQLException {
        return DbUtils.executeQuerySqlString("SELECT page_content FROM test WHERE page_id = " + id, "page_content", db).get(0);
    }

    public static void main(String[] args) throws SQLException {
        PageRetriever retriever = new PageRetriever(Pathes.PAGES_DB);
        System.out.println(retriever.getContent(20));
    }
}
