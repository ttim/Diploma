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

    public static Set<Long> getReachableCategories(long startId) {
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
                    if (!categories.contains(id)) {
                        newCategories.add(id);
                    }
                }
            }

            if ((counter++) % 1000 == 0) {
                System.out.println("Current categories size " + categories.size());
            }
        }

        return categories;
    }

    public static void main(String[] args) {
        process(null);
    }
}
