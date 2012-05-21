package ru.abishev.wiki.categories;

import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.linkifier.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;

public class Test {
    private static Linkifier linkifier;

    static {
        try {
            linkifier = new HeuristicLinkifier(new FilteringLinkifier(SimpleLinkifier.INSTANCE));
        } catch (FileNotFoundException e) {
        }
    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> getTopEntriesByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return entries;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // collect pages
        List<Integer> pages = new ArrayList<Integer>();
//        for (Status status : Tweets.DHH_200_TWEETS) {
//            for (AnchorStatistic anchor : linkifier.linkify(status.getText())) {
//                pages.add(anchor.pageId);
//            }
//        }

        System.out.println("Pages count: " + pages.size());

        // collect categories with stat
        long[] ids = new long[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
            ids[i] = pages.get(i);
        }

        Map<Category, Integer> categories = CategoriesCollector.rateCategoriesForPages(5, ids);
        System.out.println("Distinct categories size: " + categories.size());

        // print top categories
        List<Map.Entry<Category, Integer>> sortedCategories = getTopEntriesByValue(categories);
        for (int i = 0; i < 300; i++) {
            System.out.println(sortedCategories.get(i).getKey() + " -> " + sortedCategories.get(i).getValue());
        }
    }
}
