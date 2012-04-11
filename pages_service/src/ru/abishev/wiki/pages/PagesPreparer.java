package ru.abishev.wiki.pages;

import ru.abishev.Pathes;
import ru.abishev.wiki.parser.AnalyserRunner;
import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;
import ru.abishev.wiki.parser.WikiTextParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Analyse dump (articles.xml.bz2) and build index (pageId, pageTitle, redirectTo), pageTitle can be started from namespace
 */
public class PagesPreparer implements WikiDumpAnalyser {
    private final File indexOutputFile;
    private PrintWriter writer;

    public PagesPreparer(File indexOutputFile) {
        this.indexOutputFile = indexOutputFile;
    }

    @Override
    public void start() throws Exception {
        writer = new PrintWriter(new FileOutputStream(indexOutputFile));
    }

    @Override
    public void analysePage(WikiPage page) {
        String redirectTo = new WikiTextParser(page.text).parseRedirectText();
        writer.println(page.id + "|" + page.title + "|" + (redirectTo == null ? "" : redirectTo));
    }

    @Override
    public void finish() {
        writer.close();
    }

    public static void preparePages(File wikiDump, File indexOutput) throws Exception {
        AnalyserRunner.analyseXmlDump(new PagesPreparer(indexOutput), wikiDump, Integer.MAX_VALUE, true);
    }

//    public static void main(String[] args) throws Exception {
//        preparePages(Pathes.WIKI_ARTICLES_DUMP, Pathes.PAGES_INDEX);
//    }
}
