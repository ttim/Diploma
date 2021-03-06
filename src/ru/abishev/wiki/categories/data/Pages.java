package ru.abishev.wiki.categories.data;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.*;

public class Pages implements Iterable<Page> {
    public static final Pages INSTANCE = new Pages(new File("./data/preprocessed/page.csv"));

    private List<Page> pages;
    private Map<String, Page> nameToPageInMain;
    private Map<String, Page> nameToPageInCategories;
    private Map<Long, Page> idToPage;

    public Pages(File processedPagesCsv) {
        pages = new ArrayList<Page>();

        for (List<String> page : CsvUtils.readCsvIgnoreFirstLine(processedPagesCsv, '|', '\'')) {
            String name = page.get(2);
            pages.add(new Page(Long.parseLong(page.get(0)), Integer.parseInt(page.get(1)), name.substring(1, name.length() - 1)));
        }

        nameToPageInMain = new HashMap<String, Page>();
        nameToPageInCategories = new HashMap<String, Page>();
        idToPage = new HashMap<Long, Page>();
        for (Page page : pages) {
            if (page.namespace == 0) {
                if (nameToPageInMain.containsKey(page.title.toLowerCase())) {
//                    System.out.println("=(" + page.title);
                    // todo: ?
                }
                nameToPageInMain.put(page.title.toLowerCase(), page);
            } else {
                nameToPageInCategories.put(page.title.toLowerCase(), page);
            }
            idToPage.put(page.id, page);
        }

        System.out.println("Pages size: " + pages.size());
    }

    @Override
    public Iterator<Page> iterator() {
        return pages.iterator();
    }

    public Page getById(long id) {
        return idToPage.get(id);
    }

    private String convertForSearch(String s) {
        return s.toLowerCase().replaceAll(" ", "_");
    }

    public Page getByTitleInMain(String title) {
        return nameToPageInMain.get(convertForSearch(title));
    }

    public Page getByTitleInCategories(String title) {
        return nameToPageInCategories.get(convertForSearch(title));
    }
}
