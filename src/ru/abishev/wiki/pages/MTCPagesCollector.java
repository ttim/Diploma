package ru.abishev.wiki.pages;

import com.google.common.collect.Sets;
import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.MTCCollector;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;

import java.io.File;
import java.util.*;

public class MTCPagesCollector {
    public static Map<Integer, Category> getRootToPageIdsMap(File categoryLinksPagesCsv, Map<Category, Category> catToRoot) {
        Map<Integer, Category> pageToRoot = new HashMap<Integer, Category>();

        // go throw categorylinksPages
        for (List<String> e : CsvUtils.readCsvIgnoreFirstLine(categoryLinksPagesCsv, '|', '"')) {
            int pageId = Integer.parseInt(e.get(0));
            int categoryId = Integer.parseInt(e.get(1));

            if (Categories.RAW.getById(categoryId) != null) {
                Category rootCategory = catToRoot.get(Categories.RAW.getById(categoryId));

                if (rootCategory != null) {
                    pageToRoot.put(pageId, rootCategory);
                }
            }
        }

        return pageToRoot;
    }

    public static void main(String[] args) {
        Map<Category, Category> catToRoot = MTCCollector.getInnerCategories(MTCCollector.getMainTopicClassificationCategories(), Sets.newHashSet(Categories.RAW.getByName("Chronology")));
        Map<Integer, Category> pageToRoot = getRootToPageIdsMap(new File("./data/preprocessed/categorylinks_pages.csv"), catToRoot);
    }
}
