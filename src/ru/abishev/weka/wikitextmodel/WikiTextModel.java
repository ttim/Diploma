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

import java.io.File;
import java.util.*;

public class WikiTextModel extends MapFilter {
    static final boolean debug = false;

    private transient DbMap dbCache;

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
        this.dbCache = new DbMap(new File("./data/db/wikitransform_version" + VERSION + "_depth" + maxDepth), 10000);
    }

    private Set<String> getCategoriesForText(String text) {
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

        return categories;
    }

    private Set<String> extractHashtags(String text) {
        Set<String> tags = new HashSet<String>();
        for (String word : text.split(" ")) {
            if (word.startsWith("#")) {
                tags.add(word.substring(1));
            }
        }
        return tags;
    }

    @Override
    public String map(String input) {
        String result = dbCache.get(input);

        if (result == null) {
            Set<String> words = new HashSet<String>(getCategoriesForText(input));
//        words.addAll(extractHashtags(input));
            result = Joiner.on(' ').join(words);

            dbCache.put(input, result);
        }

        return result;
    }

    private static final String VERSION = "2";
}
