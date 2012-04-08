package ru.abishev.wiki.preprocessing;

import ru.abishev.utils.FileUtils;
import ru.abishev.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WikiSqlToCsvConverter {
    // todo: USE PARSER LUKE!

    private static final int CHUNK_SIZE = 10000000;
    private static final String SEPARATOR = "|";

    public static void convert(File input, File output) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(output);

        boolean isCreateTableStatement = false;

        List<String> fields = new ArrayList<String>();

        int insertsCount = 0;

        for (FileUtils.Chunk chunk : FileUtils.readFileAsChunks(input, Charset.forName("UTF-8"), CHUNK_SIZE)) {
            if (chunk.isContinue) {
                throw new RuntimeException();
            }

            String data = chunk.data;

            if (data.startsWith("INSERT INTO ")) {
                System.out.println("Processing " + (++insertsCount) + " insert");

                // parse data
                data = data.substring(data.indexOf("("));

                while (data.length() != 0) {
                    // find next ) according to escaping
                    int pos = Utils.indexOfAccordingEscaping(data, 0, ')', '\'');

                    String current = data.substring(1, pos);
                    if (data.indexOf("(", pos) == -1) {
                        break;
                    } else {
                        data = data.substring(data.indexOf("(", pos));
                    }

                    List<String> elements = new ArrayList<String>();
                    while (current.length() != 0) {
                        int index = Utils.indexOfAccordingEscaping(current, 0, ',', '\'');

                        if (index == -1) {
                            elements.add(current.trim());
                            current = "";
                        } else {
                            elements.add(current.substring(0, index).trim());
                            current = current.substring(index + 1);
                        }
                    }

                    if (elements.size() != fields.size()) {
                        throw new RuntimeException(elements.toString());
                    } else {
                        out.println(Utils.join(elements, SEPARATOR));
                    }
                }
            }

            if (data.startsWith("CREATE TABLE ")) {
                isCreateTableStatement = true;
            }

            if (isCreateTableStatement) {
                if (data.startsWith("  `")) {
                    String field = data.substring(3, data.indexOf("`", 3));
                    fields.add(field);
                }
                if (data.startsWith(")")) {
                    out.println(Utils.join(fields, SEPARATOR));
                    isCreateTableStatement = false;
                }
            }
        }

        out.flush();
    }
}
