package ru.abishev.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class FileUtils {
    public static class Chunk {
        public final boolean isContinue;
        public final String data;

        private Chunk(boolean isContinue, String data) {
            this.isContinue = isContinue;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Chunk{" +
                    "isContinue=" + isContinue +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    public static Iterator<Chunk> readAsChunks(InputStream input, final Charset charset, final int chunkSize) {
        final Reader reader = new InputStreamReader(new BufferedInputStream(input, 8192 * 128), charset);
        return new Iterator<Chunk>() {
            private static final int FILE_IS_ENDED = -1;
            private int buffer;
            private boolean isContinue = false;

            private void readToBuffer() {
                if (buffer == FILE_IS_ENDED) {
                    // nothing
                } else {
                    try {
                        buffer = reader.read();
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }

            }

            {
                readToBuffer();
            }

            @Override
            public boolean hasNext() {
                return buffer != FILE_IS_ENDED;
            }

            @Override
            public Chunk next() {
                if (buffer == FILE_IS_ENDED) {
                    throw new IllegalStateException();
                }

                StringBuilder result = new StringBuilder();
                int count = 0;

                while (buffer != FILE_IS_ENDED && ++count < chunkSize && buffer != '\n') {
                    result.append((char) buffer);
                    readToBuffer();
                }

                if (buffer != FILE_IS_ENDED) {
                    result.append((char) buffer);
                }

                Chunk chunk = new Chunk(isContinue, result.toString());

                isContinue = !(buffer == '\n' || buffer == FILE_IS_ENDED);
                readToBuffer();

                return chunk;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Iterable<Chunk> readFileAsChunks(final File file, final Charset charset, final int chunkSize) throws FileNotFoundException {
        return new Iterable<Chunk>() {
            @Override
            public Iterator<Chunk> iterator() {
                try {
                    return readAsChunks(new FileInputStream(file), charset, chunkSize);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static void main(String[] args) throws FileNotFoundException {
        for (Chunk chunk : readFileAsChunks(new File("./test.txt"), Charset.forName("UTF-8"), 10)) {
            System.out.println(chunk);
        }
    }
}
