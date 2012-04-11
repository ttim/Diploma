package ru.abishev.wiki.parser;

import edu.jhu.nlp.wikipedia.*;
import edu.jhu.nlp.wikipedia.WikiPage;
import ru.abishev.wiki.parser.analysers.MockWikiDumpAnalyser;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class AnalyserRunner {
    public static void analyseXmlDump(final WikiDumpAnalyser analyser, File bz2XmlDump, final int maxArticlesCount, final boolean printDebugInfo) throws Exception {
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

    public static void analyseSimpleDump(WikiDumpAnalyser analyser, File simpleDump, int maxArticlesCount, boolean printDebugInfo) throws Exception {
        analyser.start();
        int num = 0;

        DataInputStream inputStream = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(simpleDump))));
        while (true) {
            try {
                long id = inputStream.readLong();
                String title = inputStream.readUTF();
                int len = inputStream.readInt();
                byte[] text = new byte[len];
                inputStream.readFully(text);
                analyser.analysePage(new ru.abishev.wiki.parser.WikiPage(id, title, new String(text)));
            } catch (EOFException e) {
                break;
            }

            num++;
            if (num == maxArticlesCount) {
                break;
            }
            if (num % 1000 == 0 && printDebugInfo) {
                System.out.println(num + " articles is analysed");
            }
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

//    public static void main(String[] args) throws Exception {
//        analyseSimpleDump(new MockWikiDumpAnalyser(), new File("./data/pages-articles.dump"), 10, true);
//    }
}
