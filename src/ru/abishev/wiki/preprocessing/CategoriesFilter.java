package ru.abishev.wiki.preprocessing;


import ru.abishev.wiki.data.Categories;
import ru.abishev.wiki.data.Category;
import ru.abishev.wiki.data.SubCategories;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class CategoriesFilter {
    // leave only topics accessible from Main_topic_classifications
    // based on SubCategories.RAW && Categories.RAW
    public static void process(PrintWriter out) {
        System.out.println("Size before: " + getSize(Categories.RAW));
        Set<Long> reachable = getReachableCategories(Categories.RAW.getByName("Main_topic_classifications").id);
        System.out.println("Size after: " + reachable.size());
    }

    public static <T> int getSize(Iterable<T> iterable) {
        int size = 0;
        for (T t : iterable) {
            size++;
        }
        return size;
    }

    public static Set<Long> categoriesToRemove() {
        Set<Long> rootsToRemove = new HashSet<Long>();
        rootsToRemove.add(Categories.RAW.getByName("Container_categories").id);
        rootsToRemove.add(Categories.RAW.getByName("Tracking_categories").id);
        rootsToRemove.add(Categories.RAW.getByName("Hidden_categories").id);
        rootsToRemove.add(Categories.RAW.getByName("Redirect-Class_articles").id);
//        rootsToRemove.add(Categories.RAW.getByName("Hidden_categories").id);

        Set<Long> result = new HashSet<Long>();
        for (long id : rootsToRemove) {
            result.add(id);
            result.addAll(SubCategories.RAW.getSubCats(id));
        }

        // and some other categories
        for (Category category : Categories.RAW) {
            if (category.name.toLowerCase().contains("sockpuppet") ||
                    category.name.toLowerCase().endsWith("_templates") ||
                    category.name.toLowerCase().startsWith("WikiProject_")) {
                result.add(category.id);
            }
        }

        return result;
    }

    public static Set<Long> getReachableCategories(long startId) {
        Set<Long> toRemove = categoriesToRemove();
        System.out.println("To remove: " + toRemove.size());

        Set<Long> categories = new HashSet<Long>();
        Set<Long> newCategories = new HashSet<Long>();
        newCategories.add(startId);

        int counter = 0;

        while (!newCategories.isEmpty()) {
            long newCat = newCategories.iterator().next();
            newCategories.remove(newCat);
            categories.add(newCat);

            if (SubCategories.RAW.getSubCats(newCat) != null) {
                for (long id : SubCategories.RAW.getSubCats(newCat)) {
                    if (!categories.contains(id) && !toRemove.contains(id)) {
                        newCategories.add(id);
                    }
                }
            }

            if ((counter++) % 1000 == 0) {
                System.out.format("Current categories size %d; new categories size: %d%n",
                        categories.size(), newCategories.size());
            }
        }

        return categories;
    }

    public static void main(String[] args) {
        process(null);
    }
}
