package ru.abishev.wiki.categories.preprocessing;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class PagesPreprocessor {
    public static void process(File pagesCsv, PrintWriter out) {
        // include only 0 - Main (real articles) and 14 - Category (category) pages
        // include only page_id + page_namespace + page_title
        // include only not redirect pages
        out.println("page_id|page_namespace|page_title");
        for (List<String> page : CsvUtils.readCsvIgnoreFirstLine(pagesCsv, '|', '\'')) {
            long id = Long.parseLong(page.get(0));
            int namespace = Integer.parseInt(page.get(1));
            String title = page.get(2);
            int isRedirect = Integer.parseInt(page.get(5));

            if (namespace == 0 || namespace == 14) {
                if (isRedirect == 0) {
                    out.println(id+"|"+namespace+"|"+title);
                }
            }
        }
        out.flush();
    }
}
