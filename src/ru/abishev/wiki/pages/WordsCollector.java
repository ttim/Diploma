package ru.abishev.wiki.pages;

import ru.abishev.wiki.parser.AnalyserRunner;
import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;
import ru.abishev.wiki.parser.WikiTextParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class WordsCollector {
    public static void analyseDump(File textDump, File wordsOutput, final int maxPagesCount) throws Exception {
        AnalyserRunner.analyseSimpleDump(new AnalyserHandler(wordsOutput), textDump, maxPagesCount, true);
    }

    private static class AnalyserHandler implements WikiDumpAnalyser {
        private final File anchorsOutput;
        private PrintWriter output;

        AnalyserHandler(File anchorsOutput) {
            this.anchorsOutput = anchorsOutput;
        }

        @Override
        public void start() throws Exception {
            output = new PrintWriter(new BufferedOutputStream(new FileOutputStream(anchorsOutput)));
        }

        @Override
        public void analysePage(WikiPage page) throws Exception {
            try {
                for (String word : splitOnPunctuation(page.text.toLowerCase())) {
                    output.println(word);
                }
            } catch (Exception e) {
                System.out.println(":-( " + e);
            }
        }

        public void finish() throws IOException {
            output.close();
        }

        private String[] splitOnPunctuation(String w) {
            return w.split("[ ,.!?:\"\\(\\)\\|/=]");
        }
    }

    public static void main(String[] args) throws Exception {
        analyseDump(new File("./data/pages-articles.dump"), new File("./data/words.csv"), Integer.MAX_VALUE);
    }
}
