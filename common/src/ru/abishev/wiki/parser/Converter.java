package ru.abishev.wiki.parser;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class Converter {
    // converter from xml dump to simple one (gziped serialized data stream)
    public static void convertDump(File dumpFile, File outputFile) throws Exception {
        AnalyserRunner.analyseXmlDump(new Dumper(outputFile), dumpFile, Integer.MAX_VALUE, true);
    }

    private static class Dumper implements WikiDumpAnalyser {
        private final File output;
        private DataOutputStream outputStream;

        public Dumper(File output) {
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
            byte[] text = page.text.getBytes();
            outputStream.writeInt(text.length);
            outputStream.write(text);
//            outputStream.writeUTF(page.text);
        }

        @Override
        public void finish() throws Exception {
            outputStream.close();
        }
    }
}
