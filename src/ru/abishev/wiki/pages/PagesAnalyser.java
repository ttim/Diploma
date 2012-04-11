package ru.abishev.wiki.pages;

import ru.abishev.wiki.parser.AnalyserRunner;
import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;
import ru.abishev.wiki.parser.WikiTextParser;
import ru.abishev.wiki.categories.data.Pages;
import ru.abishev.wiki.model.AnchorsStat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class PagesAnalyser {
    public static void analyseDump(File bz2XmlDump, File statOutput, final int maxPagesCount) throws Exception {
        AnalyserRunner.analyseDump(new AnalyserHandler(statOutput), bz2XmlDump, maxPagesCount, true);
    }

    private static class AnalyserHandler implements WikiDumpAnalyser {
        private final AnchorsStat stat = new AnchorsStat();
        private final File statOutput;

        AnalyserHandler(File statOutput) {
            this.statOutput = statOutput;
        }

        @Override
        public void start() throws Exception {
            // nothing
        }

        @Override
        public void analysePage(WikiPage page) throws Exception {
            WikiTextParser parser = new WikiTextParser(page.text);

            if (parser.parseRedirectText() != null) {
                return;
            }

            for (WikiTextParser.Link link : parser.parseLinks()) {
                if (Pages.INSTANCE.getByTitleInMain(link.page) != null) {
                    stat.addAnchorToStat(link.text, Pages.INSTANCE.getByTitleInMain(link.page).id);
                }
            }
        }

        public void finish() {
            try {
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
        analyseDump(new File("./data/downloads/pages-articles.xml.bz2"), new File("./data/stat.txt"), 5000);
    }
}
