package ru.abishev.wiki.parser;

import edu.jhu.nlp.wikipedia.*;
import edu.jhu.nlp.wikipedia.WikiPage;

import java.io.File;

public class AnalyserRunner {
    public static void analyseDump(final WikiDumpAnalyser analyser, File bz2XmlDump, final int maxArticlesCount, final boolean printDebugInfo) throws Exception {
        analyser.start();
        final int[] num = {0};

        try {
            WikiXMLSAXParser.parseWikipediaDump(bz2XmlDump.getAbsolutePath(), new PageCallbackHandler() {
                @Override
                public void process(WikiPage page) {
                    try {
                        analyser.analysePage(new ru.abishev.wiki.parser.WikiPage(Long.parseLong(page.getID()), page.getTitle().trim(), page.getWikiText().trim()));
                    } catch (Exception e) {
                        throw new WrappedException(e);
                    }
                    num[0]++;
                    if (num[0] == maxArticlesCount) {
                        throw new ExceptionForExit();
                    }
                    if (num[0] % 1000 == 0 && printDebugInfo) {
                        System.out.println(num[0] + " articles is analysed");
                    }
                }
            });
        } catch (ExceptionForExit e) {
            // it's ok =)
        } catch (WrappedException e) {
            throw e.e;
        }

        if (printDebugInfo) {
            System.out.println("Finishing");
        }
        analyser.finish();
    }

    private static class ExceptionForExit extends RuntimeException {
        // ...
    }

    private static class WrappedException extends RuntimeException {
        Exception e;

        WrappedException(Exception e) {
            this.e = e;
        }
    }
}
