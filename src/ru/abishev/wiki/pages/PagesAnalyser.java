package ru.abishev.wiki.pages;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;
import ru.abishev.wiki.model.AnchorsStat;
import ru.abishev.wiki.parser.WikiTextParser;

import java.io.File;

public class PagesAnalyser {
    public static void analyseDump(File bz2XmlDump, final int maxPagesCount) throws Exception {
        AnalyserHandler handler = new AnalyserHandler(maxPagesCount);
        WikiXMLSAXParser.parseWikipediaDump(bz2XmlDump.getAbsolutePath(), handler);
        handler.finish();
    }

    private static class AnalyserHandler implements PageCallbackHandler {
        private final int maxPagesCount;
        private final AnchorsStat stat = new AnchorsStat();
        int processedCount = 0;

        AnalyserHandler(int maxPagesCount) {
            this.maxPagesCount = maxPagesCount;
        }

        @Override
        public void process(WikiPage wikiPage) {
            if (processedCount++ > maxPagesCount) {
                // todo: =(
                finish();
                System.exit(0);
            }

            WikiTextParser parser = new WikiTextParser(wikiPage.getWikiText());

            if (parser.parseRedirectText() != null) {
                return;
            }

            System.out.println(wikiPage.getLinks());
        }

        public void finish() {
            int v = 1; // just for breakpoint
        }
    }

    public static void main(String[] args) throws Exception {
        analyseDump(new File("./data/downloads/pages-articles.xml.bz2"), 20);
    }
}
