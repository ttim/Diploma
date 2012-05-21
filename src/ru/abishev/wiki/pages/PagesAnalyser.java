package ru.abishev.wiki.pages;

import ru.abishev.persistentobjects.client.PersistentObjects;
import ru.abishev.wiki.parser.AnalyserRunner;
import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;
import ru.abishev.wiki.parser.WikiTextParser;
import ru.abishev.wiki.model.AnchorsStat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class PagesAnalyser {
    public static void analyseDump(File bz2XmlDump, File statOutput, final int maxPagesCount) throws Exception {
        AnalyserRunner.analyseSimpleDump(new AnalyserHandler(statOutput), bz2XmlDump, maxPagesCount, true);
    }

    private static class AnalyserHandler implements WikiDumpAnalyser {
        private final AnchorsStat stat = new AnchorsStat();
        private final File statOutput;
        private PagesRedirecter redirecter;
        private int badCount = 0, goodCount = 0;

        AnalyserHandler(File statOutput) {
            this.statOutput = statOutput;
        }

        @Override
        public void start() throws Exception {
            PersistentObjects.setRemotePort(5678);
            redirecter = new PagesRedirecter(new File("./data/pages-index.csv").getAbsolutePath(), true);
        }

        @Override
        public void analysePage(WikiPage page) throws Exception {
            try {
                WikiTextParser parser = new WikiTextParser(page.text);

                if (parser.parseRedirectText() != null) {
                    return;
                }

                for (WikiTextParser.Link link : parser.parseLinks()) {
                    PagesRedirecter.PageResult pageResult = redirecter.redirectPage(link.page);
                    if (pageResult == null || pageResult.isBad()) {
                        if (link.page.length() > 0) {
                            pageResult = redirecter.redirectPage(Character.toTitleCase(link.page.charAt(0)) + link.page.substring(1));
                        }
                    }
                    if (pageResult == null || pageResult.isBad()) {
                        badCount++;
                    } else {
                        goodCount++;
                        stat.addAnchorToStat(link.text, pageResult.finalPageId);
                    }
                    if ((badCount + goodCount) % 10000 == 0) {
                        System.out.println("Bad count: " + badCount + "; good count: " + goodCount);
                    }
                }

                if (stat.getAllWords().size() > 500000) {
                    // compress it!
                    System.out.println("Compressing");
                    stat.compress();
                }
            } catch (Exception e) {
                System.out.println(":-( " + e);
            }
        }

        public void finish() {
            try {
                PersistentObjects.shutdown();

                PrintWriter output = new PrintWriter(statOutput);

                for (String fromWord : stat.getAllWords()) {
                    Map<Long, Integer> anchorStat = stat.getAnchorsStat(fromWord).getStat();
                    int count = 0;
                    for (int _count : anchorStat.values()) {
                        count += _count;
                    }
                    output.print(fromWord + "/" + count + ":");
                    for (long pageId : anchorStat.keySet()) {
                        output.print(" " + pageId + "/" + anchorStat.get(pageId));
                    }
                    output.println();
                }

                output.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        analyseDump(new File("./data/pages-articles.dump"), new File("./data/stat.txt"), Integer.MAX_VALUE);
    }
}
