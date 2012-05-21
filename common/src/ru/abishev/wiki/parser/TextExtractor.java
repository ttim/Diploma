package ru.abishev.wiki.parser;

import ru.abishev.Pathes;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

public class TextExtractor {
    // converter from simple dump with pages to simple dump with pages without wiki markup
    public static void extractPlainText(File dumpFile, File outputFile) throws Exception {
        AnalyserRunner.analyseSimpleDump(new Extracter(outputFile), dumpFile, Integer.MAX_VALUE, true);
    }

    private static class Extracter implements WikiDumpAnalyser {
        private final File output;
        private DataOutputStream outputStream;

        public Extracter(File output) {
            this.output = output;
        }

        @Override
        public void start() throws Exception {
            outputStream = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(output))));
        }

        @Override
        public void analysePage(WikiPage page) throws Exception {
            outputStream.writeLong(page.id);
            outputStream.writeUTF(page.title);
            byte[] text = new WikiTextParser(page.text).parsePlainText().getBytes();
            outputStream.writeInt(text.length);
            outputStream.write(text);
//            outputStream.writeUTF(page.text);
        }

        @Override
        public void finish() throws Exception {
            outputStream.close();
        }
    }

    public static void main(String[] args) throws Exception {
        extractPlainText(Pathes.WIKI_ARTICLES_SIMPLE_DUMP, Pathes.WIKI_ARTICLES_TEXT_DUMP);
    }
}
