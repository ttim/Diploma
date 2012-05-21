package ru.abishev.wiki.categories.preprocessing;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.data.Categories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryLinksPreprocessing {
    public static void process(File input, File output) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(output);

        int processedCount = 0;

        int ignored = 0;

        // example: Haskell|Functional programming|page
        // save only !2012 cat links
        out.println("page_id|cl_to|cl_type");

        Set<String> badCategories = new HashSet<String>();

        for (List<String> data : CsvUtils.readCsvIgnoreFirstLine(input, '|', '\'')) {
            if (processedCount++ % 1000000 == 0) {
                System.out.println("Processed count: " + processedCount);
            }

            if (data.size() != 7) {
                throw new RuntimeException(data.toString());
            }

            if (data.get(3).startsWith("'2012")) {
                continue;
            }

            long pageId = Long.parseLong(data.get(0));
            String categoryTitle = data.get(1);
            categoryTitle = categoryTitle.substring(1, categoryTitle.length() - 1);

            if (!Categories.RAW.containsName(categoryTitle)) {
                if (!badCategories.contains(categoryTitle)) {
//                    System.out.println("!Not contains " + categoryTitle);
                    badCategories.add(categoryTitle);
                }
                ignored++;
            } else {
                out.println(
                        pageId + "|" + Categories.RAW.getByName(categoryTitle).id + "|" + data.get(6)
                );
            }
        }

        out.flush();

        System.out.println("Bad categories size " + badCategories.size());
        System.out.println("Ignored " + ignored);
    }
}
