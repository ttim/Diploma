//package ru.abishev.wiki;
//
//import ru.abishev.utils.CsvUtils;
//import ru.abishev.wiki.data.Categories;
//import ru.abishev.wiki.data.Category;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.util.*;
//
//public class CategoryTreeBuild {
//    public static Map<Long, List<Long>> getCategoryTree(File graphCsv) {
//        Map<Long, List<Long>> catToSubcats = new HashMap<Long, List<Long>>();
//        for (List<String> data : CsvUtils.readCsvIgnoreFirstLine(graphCsv, '|', '\'')) {
//            long catId = Long.parseLong(data.get(0)), subCatId = Long.parseLong(data.get(1));
//            if (!catToSubcats.containsKey(catId)) {
//                catToSubcats.put(catId, new ArrayList<Long>());
//            }
//            catToSubcats.get(catId).add(subCatId);
//            if (Categories.INSTANCE.getById(catId) == null || Categories.INSTANCE.getById(subCatId) == null) {
//                throw new RuntimeException(catId + " " + subCatId);
//            }
//        }
//        return catToSubcats;
//    }
//
//    private static class TreeBuilder {
//        private final Map<Long, List<Long>> tree;
//        private final PrintWriter out;
//
//        private Map<Long, Integer> printedCount = new HashMap<Long, Integer>();
//        private Set<Long> badRoots = new HashSet<Long>();
//
//        private Set<Long> currentPath = new HashSet<Long>();
//
//        private int backSituations = 0;
//
//        public TreeBuilder(Map<Long, List<Long>> tree, PrintWriter out) {
//            this.tree = tree;
//            this.out = out;
//        }
//
//        private void printIndent(int indentCount) {
//            for (int i = 0; i < indentCount; i++) {
//                out.print(' ');
//            }
//        }
//
//        private void printSubtree(long root, int indent) {
//            if (currentPath.contains(root)) {
//                printIndent(indent);
//                out.println("!back to|" + root + "|" + Categories.INSTANCE.getById(root).name);
//                backSituations++;
//                return;
//            }
//
//            currentPath.add(root);
//
//            Category category = Categories.INSTANCE.getById(root);
//
//            if (category == null) {
//                badRoots.add(root);
//            } else {
//                printedCount.put(root, printedCount.containsKey(root) ? printedCount.get(root) + 1 : 1);
//
//                printIndent(indent);
//
//                out.println(root + "|" + category.name + "|" + category.subCatsCount + "|" + category.pagesCount);
//                if (tree.containsKey(root)) {
//                    for (long subcat : tree.get(root)) {
//                        if (subcat == root) {
//                            System.out.println("oO: " + root);
//                            continue;
//                        }
//                        printSubtree(subcat, indent + 2);
//                    }
//                }
//            }
//
//            currentPath.remove(root);
//        }
//
//        public void printTree() {
//            // find root categories
//            Set<Long> roots = new HashSet<Long>(tree.keySet());
//            for (List<Long> subcats : tree.values()) {
//                for (long id : subcats) {
//                    roots.remove(id);
//                }
//            }
//
//            int rootsProcessed = 0;
//            for (long root : roots) {
//                if (rootsProcessed++ % 1000 == 0) {
//                    System.out.println("Roots processed: " + rootsProcessed + "/" + roots.size());
//                }
//                printSubtree(root, 0);
//                out.flush();
//            }
//
//            // check printedCount
//            System.out.println(printedCount.size() + " " + tree.size());
//            System.out.println("Bad roots size " + badRoots.size());
//            System.out.println("Back situations count " + backSituations);
//        }
//    }
//
//    public static void printCategoryTree(PrintWriter out, Map<Long, List<Long>> tree) {
//        new TreeBuilder(tree, out).printTree();
//    }
//
//    private static class CycleFinder {
//        private final Map<Long, List<Long>> tree;
//
//        private List<Long> currentPath = new ArrayList<Long>();
//        private Set<Long> used = new HashSet<Long>();
//
//        public CycleFinder(Map<Long, List<Long>> tree) {
//            this.tree = tree;
//        }
//
//        void go(long v) {
//            if (used.contains(v)) {
//                if (currentPath.contains(v)) {
//                    Category best = Categories.INSTANCE.getById(v);
//                    for (int i = currentPath.indexOf(v); i < currentPath.size(); i++) {
//                        Category category = Categories.INSTANCE.getById(currentPath.get(i));
//                        if (best.pagesCount + best.subCatsCount * 2 > category.pagesCount + category.subCatsCount * 2) {
//                            best = category;
//                        }
//                    }
//                    if (currentPath.size() - currentPath.indexOf(v) == 2) {
//                        for (int i = currentPath.indexOf(v); i < currentPath.size(); i++) {
//                            System.out.println(Categories.INSTANCE.getById(currentPath.get(i)));
//                        }
//                        System.out.println("-----");
//                    }
////                    System.out.println("Len: " + (currentPath.size() - currentPath.indexOf(v)) + "; best category to remove: " + best);
//                }
//                return;
//            }
//
//            used.add(v);
//            currentPath.add(v);
//            if (tree.get(v) != null) {
//                for (long subCat : tree.get(v)) {
//                    go(subCat);
//                }
//            }
//            currentPath.remove(v);
//        }
//    }
//
//    private static void checkTree(Map<Long, List<Long>> categoryTree) {
//        Set<Long> notOk = new HashSet<Long>(categoryTree.keySet());
//
//        Map<Long, Long> pagesCount = new HashMap<Long, Long>();
//        for (Category category : Categories.INSTANCE) {
//            pagesCount.put(category.id, (long) category.pagesCount);
//        }
//
//        while (true) {
//            System.out.println(notOk.size());
//
//            Set<Long> newNotOk = new HashSet<Long>();
//
//            for (long id : notOk) {
//                boolean isOk = true;
//                long curPagesCount = Categories.INSTANCE.getById(id).pagesCount;
//
//                for (long subId : categoryTree.get(id)) {
//                    if (notOk.contains(subId)) {
//                        isOk = false;
//                    } else {
//                        curPagesCount += pagesCount.get(subId);
//                    }
//                }
//
//                if (!isOk) {
//                    newNotOk.add(id);
//                } else {
//                    pagesCount.put(id, curPagesCount);
//                }
//            }
//
//            if (newNotOk.size() == notOk.size()) {
//                break;
//            }
//
//            notOk = newNotOk;
//        }
//
//        // find cycles
//        CycleFinder finder = new CycleFinder(categoryTree);
//
//        for (long root : notOk) {
//            finder.go(root);
//        }
//
//
//    }
//
//    public static void main(String[] args) throws FileNotFoundException {
//        checkTree(getCategoryTree(new File("./data/enwiki-catgraph.csv")));
////        printCategoryTree(new PrintWriter(new File("./data/categories-tree.txt")),
////                getCategoryTree(new File("./data/enwiki-latest-categorylinks-subcats.csv")));
//    }
//}
