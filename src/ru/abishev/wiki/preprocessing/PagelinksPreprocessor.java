package ru.abishev.wiki.preprocessing;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.data.Pages;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class PagelinksPreprocessor {
    public static void process(File pagesCsv, PrintWriter out) {
        // include only links between 0 and 0, and from filtered pages
        out.println("from_page_id|to_page_id");
        int count = 0;
        for (List<String> pageLink : CsvUtils.readCsvIgnoreFirstLine(pagesCsv, '|', '\'')) {
            if (count % 10000 == 0) {
                System.out.println("Processed " + count + " page links.");
            }

            long fromPageId = Long.parseLong(pageLink.get(0));
            int toNamespace = Integer.parseInt(pageLink.get(1));
            String toPageTitle = pageLink.get(2);

            if (toNamespace == 0) {
                if (Pages.INSTANCE.getById(fromPageId) != null && Pages.INSTANCE.getById(fromPageId).namespace == 0) {
                    if (Pages.INSTANCE.getByTitleInMain(toPageTitle) != null) {
                        out.println(fromPageId + "|" + Pages.INSTANCE.getByTitleInMain(toPageTitle).id);
                    }
                }
            }
        }
        out.flush();
    }
}
