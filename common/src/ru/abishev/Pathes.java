package ru.abishev;

import java.io.File;

public class Pathes {
    public static final File WIKI_ARTICLES_XML_DUMP = new File("./data/downloads/pages-articles.xml.bz2");
    public static final File WIKI_ARTICLES_SIMPLE_DUMP = new File("./data/pages-articles.dump");
    public static final File WIKI_ARTICLES_TEXT_DUMP = new File("./data/pages-articles-text.dump");
    public static final File WIKI_NORMALIZED_ANCHORS = new File("./data/anchors-normalized.csv");
    public static final File WIKI_NORMALIZED_ANCHORS_STAT = new File("./data/stat_5_sorted.txt");
    public static final File WIKI_ALL_WORDS_STAT = new File("./data/all-words-stat.txt");
    public static final File PAGES_INDEX = new File("./data/pages-index.csv");
    public static final File PAGES_DB = new File("./data/db/pages");
}
