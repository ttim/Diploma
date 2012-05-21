package ru.abishev.wiki.categories.processing;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class GraphCreator {
    public static void createGraph(File subcatsInfoCsv, PrintWriter out) throws FileNotFoundException {
//        Set<String> ignoredCats = new HashSet<String>();
//        for (FileUtils.Chunk chunk : FileUtils.readFileAsChunks(ignoreCatsCsv, Charset.forName("UTF-8"), 100000)) {
//            if (chunk.isContinue) {
//                throw new RuntimeException(chunk.data);
//            }
//            ignoredCats.add(chunk.data.trim());
//        }
//        System.out.println("Ignoring cats: " + ignoredCats);

        out.println("cat_id|subcat_id");
        Map<Long, List<Long>> catToSubcats = new TreeMap<Long, List<Long>>();
        Map<Long, List<Long>> subcatToCats = new TreeMap<Long, List<Long>>();
        for (List<String> data : CsvUtils.readCsv(subcatsInfoCsv, '|', '\'')) {
            Category subCategory = Categories.RAW.getByPageId(Long.parseLong(data.get(0)));

            if (subCategory != null) {
                long categoryId = Long.parseLong(data.get(1));
                long subCategoryId = subCategory.id;

                if (Categories.RAW.getById(subCategoryId) != null && Categories.RAW.getById(categoryId) != null) {
                    if (!catToSubcats.containsKey(categoryId)) {
                        catToSubcats.put(categoryId, new ArrayList<Long>());
                    }
                    if (!subcatToCats.containsKey(subCategoryId)) {
                        subcatToCats.put(subCategoryId, new ArrayList<Long>());
                    }
                    catToSubcats.get(categoryId).add(subCategoryId);
                    subcatToCats.get(subCategoryId).add(categoryId);
                }
            }
        }
//
//        // remove all hidden categories
//        Category hidden = Categories.INSTANCE.getByName("Hidden_categories");
//        Set<Long> toRemove = new HashSet<Long>();
//        toRemove.add(hidden.id);
//        toRemove.addAll(catToSubcats.get(hidden.id));
//
//        // remove all redirect-class articles
//        Category redirectClass = Categories.INSTANCE.getByName("Redirect-Class_articles");
//        toRemove.add(redirectClass.id);
//        toRemove.addAll(catToSubcats.get(redirectClass.id));
//
//        // remove all categories with sockpuppets word
//        for (Category category : Categories.INSTANCE) {
//            if (category.name.toLowerCase().contains("sockpuppet") ||
//                    category.name.toLowerCase().endsWith("_templates") ||
//                    category.name.toLowerCase().startsWith("WikiProject_")) {
//                toRemove.add(category.id);
//            }
//        }
//
//        // find everything that have in superCats just toRemove cats
//        boolean isChanged = true;
//        while (isChanged) {
//            System.out.println("To remove: " + toRemove.size());
//
//            isChanged = false;
//            for (Category category : Categories.INSTANCE) {
//                if (subcatToCats.containsKey(category.id)) {
//                    if (!toRemove.contains(category.id)) {
//                        boolean isToRemove = true;
//                        for (long id : subcatToCats.get(category.id)) {
//                            if (!toRemove.contains(id)) {
//                                isToRemove = false;
//                                break;
//                            }
//                        }
//                        if (isToRemove) {
//                            isChanged = true;
//                            toRemove.add(category.id);
////                            System.out.println(category);
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println("To remove: " + toRemove.size());

        for (long categoryId : catToSubcats.keySet()) {
            Collections.sort(catToSubcats.get(categoryId));
            for (long subCatId : catToSubcats.get(categoryId)) {
                out.println(categoryId + "|" + subCatId);
            }
        }

        out.flush();
    }
}
