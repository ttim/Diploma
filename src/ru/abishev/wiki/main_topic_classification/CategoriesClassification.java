package ru.abishev.wiki.main_topic_classification;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.categories.data.SubCategories;

import java.util.*;

public class CategoriesClassification {
    public static List<Category> getSubcategories(@Nullable Category category) {
        if (category == null || SubCategories.RAW.getSubCats(category.id) == null) {
            return Collections.emptyList();
        }
        List<Category> result = new ArrayList<Category>();
        for (long id : SubCategories.RAW.getSubCats(category.id)) {
            result.add(Categories.RAW.getById(id));
        }
        return result;
    }

    public static List<Category> getMainTopicClassificationCategories() {
        return getSubcategories(Categories.RAW.getByName("Main_topic_classifications"));
    }

    public static Map<Category, Category> getInnerCategories(List<Category> roots, Set<Category> forbidden) {
        Map<Category, Integer> catToLength = new HashMap<Category, Integer>();
        Map<Category, Category> catToRoot = new HashMap<Category, Category>();

        // init by roots
        Set<Category> current = new HashSet<Category>();
        for (Category root : roots) {
            if (!forbidden.contains(root)) {
                catToLength.put(root, 0);
                catToRoot.put(root, root);
                current.add(root);
            }
        }
        int currentLength = 0;

        int innerCollisionsCount = 0, outerCollisionsCount = 0;
        int innerDelta = 0, outerDelta = 0;

        while (!current.isEmpty()) {
            currentLength++;

            Set<Category> newCats = new HashSet<Category>();

            for (Category category : current) {
                for (Category subCat : getSubcategories(category)) {
                    if (!forbidden.contains(subCat)) {
                        if (catToLength.containsKey(subCat)) {
                            // ?
                            int delta = currentLength - catToLength.get(subCat);
                            if (catToRoot.get(subCat).equals(catToRoot.get(category))) {
                                innerCollisionsCount++;
                                innerDelta += delta;
                            } else {
                                outerCollisionsCount++;
                                outerDelta += delta;
                            }
                        } else {
                            catToLength.put(subCat, currentLength);
                            catToRoot.put(subCat, catToRoot.get(category));
                            newCats.add(subCat);
                        }
                    }
                }
            }

            current = newCats;
        }

        System.out.println("inner collisions count / delta sum: " + innerCollisionsCount + " / " + innerDelta);
        System.out.println("outer collisions count / delta sum: " + outerCollisionsCount + " / " + outerDelta);

        return catToRoot;
    }

    private static double[] add(@NotNull double[] fst, @Nullable double[] snd) {
        double[] result = new double[fst.length];

        for (int i = 0; i < fst.length; i++) {
            result[i] += fst[i];
            if (snd != null) {
                result[i] += snd[i];
            }
        }

        return result;
    }

    public static Map<Category, Category> getInnerCategories2(List<Category> roots) {
        Map<Category, double[]> rank = new HashMap<Category, double[]>();
        Map<Category, double[]> currentRank = new HashMap<Category, double[]>();

        double sum = 1000000;

        // init
        for (Category root : roots) {
            currentRank.put(root, new double[roots.size()]);
            currentRank.get(root)[roots.indexOf(root)] = sum / roots.size();
        }

        while (sum > 300) {
            System.out.println("Current sum " + sum);

            // add currentRank to rank
            for (Category category : currentRank.keySet()) {
                rank.put(category, add(currentRank.get(category), rank.get(category)));
            }

            // update currentRank
            Map<Category, double[]> newRank = new HashMap<Category, double[]>();
            for (Category category : currentRank.keySet()) {
                List<Category> subCats = getSubcategories(category);
                double[] addition = new double[roots.size()];
                for (int i = 0; i < roots.size(); i++) {
                    addition[i] = currentRank.get(category)[i] / subCats.size();
                }
                for (Category subCat : subCats) {
                    newRank.put(subCat, add(addition, newRank.get(subCat)));
                }
            }

            currentRank = newRank;
            // calc sum
            sum = 0;
            for (double[] p : currentRank.values()) {
                for (double v : p) {
                    sum += v;
                }
            }
        }

        return null;
    }


    public static void main(String[] args) {
        List<Category> mtcCategories = getMainTopicClassificationCategories();
//        mtcCategories.remove(Categories.RAW.getByName("Mathematics"));
//        mtcCategories.add(0, Categories.RAW.getByName("Mathematics"));
        Map<Category, Category> categoryToRoot = getInnerCategories(mtcCategories, Sets.newHashSet(Categories.RAW.getByName("Chronology")));
        categoryToRoot = getInnerCategories2(mtcCategories);
    }
}
