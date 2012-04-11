package ru.abishev.wiki.categories.preprocessing;

import ru.abishev.utils.CsvUtils;
import ru.abishev.utils.Utils;
import ru.abishev.wiki.categories.data.Page;
import ru.abishev.wiki.categories.data.Pages;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class CategoriesPreprocessing {
    public static void process(File categoriesCsv, PrintWriter out) {
        // include only where cat_pages+cat_subcast > 0 && page != null
        // and not hidden
        out.println("cat_id|cat_title|cat_pages|cat_subcast|cat_files|page_id");

        for (List<String> data : CsvUtils.readCsvIgnoreFirstLine(categoriesCsv, '|', '\'')) {
            if (data.size() != 6) {
                throw new RuntimeException(data.toString());
            }

            if (data.get(5).equals("1")) {
                System.out.println(data);
            } else {
                if (Integer.parseInt(data.get(2)) + Integer.parseInt(data.get(3)) > 0) {
                    String name = data.get(1);
                    name = name.substring(1, name.length() - 1);

                    Page page = Pages.INSTANCE.getByTitleInCategories(name);

                    if (page != null) {
                        data.remove(5);
                        out.println(Utils.join(data, "|") + "|" + page.id);
                    }
                }
            }
        }

        out.flush();
    }
}
