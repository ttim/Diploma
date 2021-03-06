package ru.abishev.wiki.example2;

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

public class ComputerProgramming {
    public static Set<Category> collectComputerProgrammingCategories() {
        Set<Category> categories = new HashSet<Category>();
        for (long id : CategoriesFilter.getReachableCategories(Categories.RAW.getByName("Programming_languages").id)) {
            categories.add(Categories.RAW.getById(id));
        }
        return categories;
    }

    public static Set<Long> collectComputerProgrammingArticles() {
        Set<Long> result = new HashSet<Long>();
        Set<Long> categories = new HashSet<Long>();
        for (Category c : collectComputerProgrammingCategories()) {
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
//        Set<Category> categories = collectComputerProgrammingCategories();
//        System.out.println(categories.size() + " / " + categories);

//        Set<Long> articles = collectComputerProgrammingArticles();
//
//        PrintWriter out = new PrintWriter(new File("./data/prog_ids.csv"));
//        for (long id : articles) {
//            if (Pages.INSTANCE.getById(id) == null) {
//                System.out.println("Bad " + id);
//                continue;
//            }
//            out.println(id + "|" + Pages.INSTANCE.getById(id).title);
//        }
//        out.close();

        Set<Long> articles = new HashSet<Long>();
        for (List<String> data : CsvUtils.readCsv(new File("./data/prog_ids.csv"), '|', '"')) {
            articles.add(Long.parseLong(data.get(0)));
        }

        List<String> anchors = collectAnchors(articles);
        PrintWriter out = new PrintWriter(new File("./data/prog_anchors.csv"));
        for (String anchor : anchors) {
            out.println(anchor);
        }
        out.close();
    }
}
