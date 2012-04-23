package ru.abishev.wiki.pages;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.MTCCollector;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;

import java.io.File;
import java.util.*;

public class MTCPagesCollector {
    public static Map<Category, Set<Integer>> getRootToPageIdsMap(File categoryLinksPagesCsv, Map<Category, Set<Category>> rootToCategories) {
        Map<Category, Set<Integer>> rootToPageIds = new HashMap<Category, Set<Integer>>();

        Map<Category, Category> categoryToRoot = new HashMap<Category, Category>();
        for (Category root : rootToCategories.keySet()) {
            for (Category category : rootToCategories.get(root)) {
                categoryToRoot.put(category, root);
            }
            rootToPageIds.put(root, new HashSet<Integer>());
        }

        // go throw categorylinksPages
        for (List<String> e : CsvUtils.readCsvIgnoreFirstLine(categoryLinksPagesCsv, '|', '"')) {
            int pageId = Integer.parseInt(e.get(0));
            int categoryId = Integer.parseInt(e.get(1));

            if (Categories.RAW.getById(categoryId) != null) {
                Category rootCategory = categoryToRoot.get(Categories.RAW.getById(categoryId));

                if (rootCategory != null) {
                    rootToPageIds.get(rootCategory).add(pageId);
                }
            }
        }

        return rootToPageIds;
    }

    public static void main(String[] args) {
        Map<Category, Set<Category>> rootToCategories = MTCCollector.getInnerCategories(MTCCollector.getMainTopicClassificationCategories());
        Map<Category, Set<Integer>> rootToPages = getRootToPageIdsMap(new File("./data/preprocessed/categorylinks_pages.csv"), rootToCategories);
    }
}
