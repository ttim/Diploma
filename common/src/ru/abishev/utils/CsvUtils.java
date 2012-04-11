package ru.abishev.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvUtils {
    public static Iterator<List<String>> readCsv(final InputStream input, final char separator, final char strCharacter) {
       final Iterator<FileUtils.Chunk> inner = FileUtils.readAsChunks(input, Charset.forName("utf-8"), 1000000);

       return new Iterator<List<String>>() {
           @Override
           public boolean hasNext() {
               return inner.hasNext();
           }

           @Override
           public List<String> next() {
               FileUtils.Chunk chunk = inner.next();
               if (chunk.isContinue) {
                   throw new RuntimeException(":-(");
               }

               String data = chunk.data;
               List<String> result = new ArrayList<String>();

               while (true) {
                   int index= Utils.indexOfAccordingEscaping(data, 0, separator, strCharacter);
                   if (index == -1) {
                       result.add(data.trim());
                       break;
                   } else {
                       result.add(data.substring(0, index));
                       data = data.substring(index+1);
                   }
               }

               return result;
           }

           @Override
           public void remove() {
               inner.remove();
           }
       };
    }

    public static Iterable<List<String>> readCsv(final File file, final char separator, final char strCharacter) {
        return new Iterable<List<String>>() {
            @Override
            public Iterator<List<String>> iterator() {
                try {
                    return readCsv(new FileInputStream(file), separator, strCharacter);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<List<String>> readCsvIgnoreFirstLine(final File file, final char separator, final char strCharacter) {
        return new Iterable<List<String>>() {
            @Override
            public Iterator<List<String>> iterator() {
                try {
                    Iterator<List<String>> iterator = readCsv(new FileInputStream(file), separator, strCharacter);
                    iterator.next();
                    return iterator;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
