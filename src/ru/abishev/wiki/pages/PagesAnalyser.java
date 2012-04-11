package ru.abishev.wiki.pages;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;
import ru.abishev.wiki.parser.WikiTextParser;
import ru.abishev.wiki.categories.data.Pages;
import ru.abishev.wiki.model.AnchorsStat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class PagesAnalyser {
    public static void analyseDump(File bz2XmlDump, File statOutput, final int maxPagesCount) throws Exception {
        AnalyserHandler handler = new AnalyserHandler(statOutput, maxPagesCount);
        WikiXMLSAXParser.parseWikipediaDump(bz2XmlDump.getAbsolutePath(), handler);
        handler.finish();
    }

    private static class AnalyserHandler implements PageCallbackHandler {
        private final int maxPagesCount;
        private final AnchorsStat stat = new AnchorsStat();
        private final File statOutput;

        int processedCount = 0;

        AnalyserHandler(File statOutput, int maxPagesCount) {
            this.maxPagesCount = maxPagesCount;
            this.statOutput = statOutput;
        }

        @Override
        public void process(WikiPage wikiPage) {
            if (processedCount++ > maxPagesCount) {
                // todo: =(
                finish();
                System.exit(0);
            }
            if (processedCount % 1000 == 0) {
                System.out.println(processedCount + " articles processed");
            }


            WikiTextParser parser = new WikiTextParser(wikiPage.getWikiText());

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
            System.out.println("Finishing");

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
