package ru.abishev.wiki.categories;

import java.io.File;
import java.sql.SQLException;

import static ru.abishev.wiki.categories.DbUtils.executeSql;

public class PageCategoriesPreprocessor {
    public static void preprocess(File categoryLinksFile, File outputDbFile) throws ClassNotFoundException, SQLException {
        String sql = "CREATE TABLE TEST(page_id INT, cl_to INT, cl_type VARCHAR(255)) AS SELECT * FROM CSVREAD('" + categoryLinksFile.getAbsolutePath() + "', null, 'fieldSeparator=|');";
        System.out.println("Sql to execute: " + sql);
        executeSql(sql, outputDbFile);
    }

    public static void addIndex(File dbFile) throws SQLException, ClassNotFoundException {
        String sql = "CREATE HASH INDEX ON TEST(page_id);";
        System.out.println("Sql to execute: " + sql);
        executeSql(sql, dbFile);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        preprocess(new File("./data/preprocessed/categorylinks_pages.csv"), new File("./data/db/categorylinks_pages"));
//        addIndex(new File("./data/db/categorylinks_pages"));
    }
}
