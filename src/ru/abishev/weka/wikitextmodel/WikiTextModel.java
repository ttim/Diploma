package ru.abishev.weka.wikitextmodel;

import com.google.common.base.Joiner;
import ru.abishev.wiki.categories.CategoriesCollector;
import ru.abishev.wiki.categories.data.Category;
import weka.core.*;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.SimpleStreamFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.*;

public class WikiTextModel extends MapFilter {
    static final boolean debug = false;

    private static Map<String, Set<String>> textToCategoriesCache = new HashMap<String, Set<String>>();

    private final int maxDepth;

    private static final int MAX_CATEGORIES_COUNT = 10000;

    public static Filter getWikiTextModel(int maxDepth, String textNum) {
        WikiTextModel preprocess = new WikiTextModel(maxDepth);
        preprocess.setAttributeIndex(textNum);

        StringToWordVector process = new StringToWordVector();
        process.setWordsToKeep(MAX_CATEGORIES_COUNT);
        process.setAttributeIndices(textNum);

        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(new Filter[]{preprocess, process});

        return multiFilter;
    }

    private WikiTextModel(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private Set<String> getCategoriesForText(String text) {
        if (textToCategoriesCache.containsKey(text)) {
            return textToCategoriesCache.get(text);
        }

        if (debug) {
            System.out.println("get categories for " + text);
        }

        // first - get pages
        Set<Integer> pages = LinkifierAlgo.linkifyWithLimit(text);

        // second - get categories
        Set<String> categories = new HashSet<String>();
        for (int pageId : pages) {
            try {
                for (Category category : CategoriesCollector.collectCategories(pageId, maxDepth)) {
                    categories.add(category.name);
                }
            } catch (Exception e) {
                // todo: ?
                e.printStackTrace();
            }
        }

        if (debug) {
            System.out.println("categories " + categories);
            System.out.println();
        }

        textToCategoriesCache.put(text, categories);

        return categories;
    }

    @Override
    public String map(String input) {
        Set<String> words = new HashSet<String>(getCategoriesForText(input));
//        for (String word : input.split(" ")) {
//            if (word.startsWith("#")) {
//                words.add(word.substring(1));
//            }
//        }
        return Joiner.on(' ').join(words);
    }
}
