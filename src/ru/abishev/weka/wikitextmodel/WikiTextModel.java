package ru.abishev.weka.wikitextmodel;

import ru.abishev.wiki.categories.CategoriesCollector;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.linkifier.*;
import weka.core.*;
import weka.filters.SimpleStreamFilter;

import java.util.*;

public class WikiTextModel extends SimpleStreamFilter {
    static final boolean debug = true;

    private static Map<String, Set<String>> textToCategoriesCache = new HashMap<String, Set<String>>();

    private static final int MAX_CATEGORIES_COUNT = 10000;
    private static final String CATEGORY_PREFIX = "__c_";

    private static Map<String, Integer> categoryNameToInnerId = new HashMap<String, Integer>();
    private static int currentId = 1;

    private final String textFieldName;
    private final int maxDepth;
    private int textFieldIndex = -1;
    private int categoriesOffset = -1;

    private Instances structure;

    public WikiTextModel(String textFieldName, int maxDepth) {
        this.textFieldName = textFieldName;
        this.maxDepth = maxDepth;
    }

    @Override
    public String globalInfo() {
        return "Wiki-based text model";
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        FastVector attrInfo = new FastVector();
        for (int i = 0; i < inputFormat.numAttributes(); i++) {
            Attribute attribute = inputFormat.attribute(i);
            if (!attribute.name().equals(textFieldName)) {
                attrInfo.addElement(attribute);
            } else {
                textFieldIndex = attribute.index();
            }
        }
        if (textFieldIndex != -1) {
            categoriesOffset = attrInfo.size();
            // add empty fields for categories
            for (int i = 1; i <= MAX_CATEGORIES_COUNT; i++) {
                attrInfo.addElement(new Attribute(CATEGORY_PREFIX + i));
            }
        } else {
            if (debug) {
                System.out.println("no text field...");
            }
        }
        structure = new Instances(inputFormat.relationName() + "-wiki", attrInfo, 10000);
        structure.setClass(structure.attribute(inputFormat.classAttribute().index()));
        return structure;
    }

    @Override
    protected Instance process(Instance input) throws Exception {
        List<Double> attValues = new ArrayList<Double>();
        List<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < input.numAttributes(); i++) {
            if (i == textFieldIndex) {
                String text = input.stringValue(textFieldIndex);
                for (String category : getCategoriesForText(text)) {
                    if (!categoryNameToInnerId.containsKey(category)) {
                        // try to add...
                        if (currentId > MAX_CATEGORIES_COUNT) {
                            continue;
                        }
                        categoryNameToInnerId.put(category, currentId++);
                    }

                    if (categoryNameToInnerId.containsKey(category)) {
                        indices.add(categoryNameToInnerId.get(category) + categoriesOffset);
                        attValues.add(1d);
                    }
                }
            } else {
                indices.add(i);
                Attribute attribute = input.attribute(i);
//                if (attribute.isNumeric()) {
                attValues.add(input.value(i));
//                }
//                attValues.add(input.attribute(i).indexOfValue(input.));
            }
        }

        double[] attValuesArr = new double[attValues.size()];
        int[] indicesArr = new int[indices.size()];
        for (int i = 0; i < attValues.size(); i++) {
            attValuesArr[i] = attValues.get(i);
            indicesArr[i] = indices.get(i);
        }
//        System.out.println(indices + " / " + attValues);
        Instance result = new SparseInstance(input.weight(), attValuesArr, indicesArr, 1000);
        return result;
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
}
