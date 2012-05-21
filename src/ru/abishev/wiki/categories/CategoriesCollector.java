package ru.abishev.wiki.categories;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.categories.data.SubCategories;
import ru.abishev.wiki.main_topic_classification.Rank;
import ru.abishev.wiki.main_topic_classification.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CategoriesCollector {
    public static final List<Category> MTC_CATEGORIES = Utils.getSubcategories(Categories.RAW.getByName("Main_topic_classifications"));
    public static final Set<Category> FORBIDDEN_CATEGORIES = new HashSet<Category>();

    static {
        FORBIDDEN_CATEGORIES.addAll(Utils.getSubcategories(Categories.RAW.getByName("Articles")));
        FORBIDDEN_CATEGORIES.addAll(Utils.getSubcategories(Categories.RAW.getByName("Contents")));
        FORBIDDEN_CATEGORIES.addAll(Utils.getSubcategories(Categories.RAW.getByName("Hidden_categories")));
        FORBIDDEN_CATEGORIES.addAll(Utils.getSubcategories(Categories.RAW.getByName("Fundamental_categories")));
//        FORBIDDEN_CATEGORIES.addAll(Utils.getSubcategories(Categories.RAW.getByName("Fundamental_categories")));
    }

    public static Set<Category> collectCategories(long pageId, int maxDepth) throws ClassNotFoundException, SQLException {
        // first - get categories for page
        Set<Category> categories = new HashSet<Category>();
        for (Category category : getPageCategories(pageId)) {
            categories.addAll(getUpCategories(category, maxDepth));
        }
        return categories;
    }

    public static Map<Category, Integer> rateCategoriesForPages(int maxDepth, long... pages) throws ClassNotFoundException, SQLException {
        Map<Category, Integer> result = new HashMap<Category, Integer>();
        for (long pageId : pages) {
            for (Category category : collectCategories(pageId, maxDepth)) {
                if (!result.containsKey(category)) {
                    result.put(category, 0);
                }
                result.put(category, result.get(category) + 1);
            }
        }
        return result;
    }

    public static List<Category> getPageCategories(long pageId) throws ClassNotFoundException, SQLException {
        List<Category> result = new ArrayList<Category>();
        for (int id : DbUtils.executeQuerySql("SELECT cl_to FROM test WHERE page_id = " + pageId, "cl_to", Integer.class, new File("./data/db/categorylinks_pages"))) {
            if (Categories.RAW.getById(id) != null) {
                result.add(Categories.RAW.getById(id));
            }
        }
        return result;
    }

    public static Set<Category> getUpCategories(Category category, int maxDepth) {
        Set<Category> result = new HashSet<Category>();
        Set<Category> processed = new HashSet<Category>();
        getUpCategories(category, result, processed, maxDepth);
        return result;
    }

    private static void getUpCategories(Category category, Set<Category> current, Set<Category> processed, int maxDepth) {
        if (maxDepth == 0) {
            return;
        }

        if (processed.contains(category)) {
            return;
        }

        processed.add(category);

        if (MTC_CATEGORIES.contains(category)) {
            current.add(category);
        }

        if (FORBIDDEN_CATEGORIES.contains(category)) {
            return;
        }

        current.add(category);
        if (SubCategories.RAW.getSuperCats(category.id) != null) {
            for (long superCategory : SubCategories.RAW.getSuperCats(category.id)) {
                if (Categories.RAW.getById(superCategory) != null) {
                    getUpCategories(Categories.RAW.getById(superCategory), current, processed, maxDepth - 1);
                }
            }
        }
    }

    public static void testPageCategories() throws ClassNotFoundException, SQLException {
        int pageId = 27404990; // haskell programming language
        getPageCategories(pageId); // just for static init
        System.out.println("Getting categories for page " + pageId);
        List<Category> categories = getPageCategories(pageId);
        for (Category category : categories) {
            System.out.println(category);
        }
    }

    public static void testPageCategories2() throws ClassNotFoundException, SQLException {
        int pageId = 27404990; // haskell programming language
        getPageCategories(pageId); // just for static init
        System.out.println("Getting categories for page " + pageId);
        Set<Category> categories = collectCategories(5, pageId);
        for (Category category : categories) {
            System.out.println(category);
        }
    }

    public static void testUpCategories() {
        Category programmingLanguages = Categories.RAW.getByName("Programming_languages");
        for (Category category : getUpCategories(programmingLanguages, 5)) {
            System.out.println(category);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        testPageCategories();
//        testUpCategories();
        testPageCategories2();
    }
}
