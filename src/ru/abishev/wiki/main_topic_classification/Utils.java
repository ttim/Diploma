package ru.abishev.wiki.main_topic_classification;

import org.jetbrains.annotations.Nullable;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;
import ru.abishev.wiki.categories.data.SubCategories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
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
}
