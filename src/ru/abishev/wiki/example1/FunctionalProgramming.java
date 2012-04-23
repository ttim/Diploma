package ru.abishev.wiki.example1;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.categories.processing.CategoriesFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionalProgramming {
    public static Set<Category> collectFunctionalProgrammingCategories() {
        Set<Category> categories = new HashSet<Category>();
        for (long id : CategoriesFilter.getReachableCategories(Categories.RAW.getByName("Functional_programming").id)) {
            categories.add(Categories.RAW.getById(id));
        }
        return categories;
    }

    public static Set<Long> collectFunctionalProgrammingArticles() {
        Set<Long> result = new HashSet<Long>();
        Set<Long> categories = new HashSet<Long>();
        for (Category c : collectFunctionalProgrammingCategories()) {
            categories.add(c.id);
        }
        int count = 0;
        for (List<String> data : CsvUtils.readCsvIgnoreFirstLine(new File("./data/preprocessed/categorylinks_pages.csv"), '|', '"')) {
            long pageId = Long.parseLong(data.get(0));
            long categoryId = Long.parseLong(data.get(1));
            if (categories.contains(categoryId)) {
                result.add(pageId);
            }
            if (count++ % 1000000 == 0) {
                System.out.println(count + " is processed");
            }
        }

        System.out.println("Articles collected: " + result.size());

        return result;
    }

    public static List<String> collectAnchors(Set<Long> articles) {
        List<String> result = new ArrayList<String>();
        Set<Long> pages = new HashSet<Long>();
        for (long id : articles) {
            pages.add(id);
        }
        int count = 0;
        for (List<String> data : CsvUtils.readCsv(new File("./data/anchors-normalized.csv"), '|', '"')) {
            long pageId = Long.parseLong(data.get(1));
            String anchor = data.get(0);
            if (pages.contains(pageId)) {
                result.add(anchor);
            }
            if (count++ % 1000000 == 0) {
                System.out.println(count + " is processed");
            }
        }

        System.out.println("Anchors collected: " + result.size());

        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
//        Set<Category> categories = collectFunctionalProgrammingCategories();
//        // http://en.wikipedia.org/wiki/Category:Spreadsheet_software =)
//        System.out.println(categories.size() + " / " + categories);

        // collect articles
//        Set<Long> articles = collectFunctionalProgrammingArticles();
//
//        PrintWriter out = new PrintWriter(new File("./data/fprog_ids.csv"));
//        for (long id : articles) {
//            if (Pages.INSTANCE.getById(id) == null) {
//                System.out.println("Bad " + id);
//                continue;
//            }
//            out.println(id + "|" + Pages.INSTANCE.getById(id).title);
//        }
//        out.close();

        Set<Long> articles = new HashSet<Long>();
        for (List<String> data : CsvUtils.readCsv(new File("./data/fprog_ids.csv"), '|', '"')) {
            articles.add(Long.parseLong(data.get(0)));
        }

        List<String> anchors = collectAnchors(articles);
        PrintWriter out = new PrintWriter(new File("./data/fprog_anchors.csv"));
        for (String anchor : anchors) {
            out.println(anchor);
        }
        out.close();
    }
}
