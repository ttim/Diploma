//package ru.abishev.wiki.main_topic_classification;
//
//import com.google.common.collect.Sets;
//import ru.abishev.utils.CsvUtils;
//import ru.abishev.wiki.categories.data.Categories;
//import ru.abishev.wiki.categories.data.Category;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.util.List;
//import java.util.Map;
//
//public class AnchorsClassification {
//    public static void processAnchors(File anchorsStat, File outputFile, Map<Integer, Category> pageToRoot) throws FileNotFoundException {
//        PrintWriter output = new PrintWriter(outputFile);
//
//        int num = 0;
//        for (List<String> anchor : CsvUtils.readCsv(anchorsStat, '|', '"')) {
//            if (num++ % 1000000 == 0) {
//                System.out.println("At " + num + " count");
//            }
//
//            if (anchor.size() != 3) {
//                System.out.println(":-( " + anchor);
//                continue;
//            }
//
//            String anchorText = anchor.get(0);
//            int pageId = Integer.parseInt(anchor.get(1));
//            int count = anchor.size() == 3 ? Integer.parseInt(anchor.get(2)) : 1;
//
//            if (pageToRoot.containsKey(pageId)) {
//                Category root = pageToRoot.get(pageId);
//                output.println(anchorText + "|" + root.id + "|" + count + "|" + root.name);
//            }
//        }
//
//        output.close();
//    }
//
//    public static void main(String[] args) throws FileNotFoundException {
//        Map<Category, Category> catToRoot = CategoriesClassification.getInnerCategories(CategoriesClassification.getMainTopicClassificationCategories(), Sets.newHashSet(Categories.RAW.getByName("Chronology")));
//        Map<Integer, Category> pageToRoot = PagesClassification.getRootToPageIdsMap(new File("./data/preprocessed/categorylinks_pages.csv"), catToRoot);
//        processAnchors(new File("./data/stat_5_sorted.txt"), new File("./data/roots_anchors.txt"), pageToRoot);
//    }
//}
