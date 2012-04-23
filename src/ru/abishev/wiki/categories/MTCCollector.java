package ru.abishev.wiki.categories;

import org.jetbrains.annotations.Nullable;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.categories.data.SubCategories;

import java.util.*;

public class MTCCollector {
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

    public static Map<Category, Set<Category>> getInnerCategories(List<Category> roots) {
        Map<Category, Integer> catToLength = new HashMap<Category, Integer>();
        Map<Category, Category> catToRoot = new HashMap<Category, Category>();

        // init by roots
        for (Category root : roots) {
            catToLength.put(root, 0);
            catToRoot.put(root, root);
        }
        Set<Category> current = new HashSet<Category>(roots);
        int currentLength = 0;

        int innerCollisionsCount = 0, outerCollisionsCount = 0;
        int innerDelta = 0, outerDelta = 0;

        while (!current.isEmpty()) {
            currentLength++;

            Set<Category> newCats = new HashSet<Category>();

            for (Category category : current) {
                for (Category subCat : getSubcategories(category)) {
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

            current = newCats;
        }

        System.out.println("inner collisions count / delta sum: " + innerCollisionsCount + " / " + innerDelta);
        System.out.println("outer collisions count / delta sum: " + outerCollisionsCount + " / " + outerDelta);

        // calc result
        Map<Category, Set<Category>> result = new HashMap<Category, Set<Category>>();
        for (Category root : roots) {
            result.put(root, new HashSet<Category>());
        }
        for (Map.Entry<Category, Category> categoryToRoot : catToRoot.entrySet()) {
            result.get(categoryToRoot.getValue()).add(categoryToRoot.getKey());
        }
        return result;
    }

    public static void main(String[] args) {
        List<Category> mtcCategories = getMainTopicClassificationCategories();
//        mtcCategories.remove(Categories.RAW.getByName("Mathematics"));
//        mtcCategories.add(0, Categories.RAW.getByName("Mathematics"));
//        Map<Category, Set<Category>> categoriesByRoot = getInnerCategories(mtcCategories);
    }
}
