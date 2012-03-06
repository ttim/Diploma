package ru.abishev.wiki.data;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SubCategories {
    public static SubCategories RAW = new SubCategories(new File("./data/enwiki-latest-categorylinks-subcats.csv"));
    
    private Map<Long, List<Long>> catToSubcats = new TreeMap<Long, List<Long>>();
    private Map<Long, List<Long>> subcatToCats = new TreeMap<Long, List<Long>>();

    public SubCategories(File subCatsFileCsv) {
        System.out.println("Init from " + subCatsFileCsv);

        for (List<String> data : CsvUtils.readCsv(subCatsFileCsv, '|', '\'')) {
            Category subCategory = Categories.RAW.getByPageId(Long.parseLong(data.get(0)));

            if (subCategory != null) {
                long categoryId = Long.parseLong(data.get(1));
                long subCategoryId = subCategory.id;

                if (Categories.RAW.getById(subCategoryId) != null && Categories.RAW.getById(categoryId) != null) {
                    if (!catToSubcats.containsKey(categoryId)) {
                        catToSubcats.put(categoryId, new ArrayList<Long>());
                    }
                    if (!subcatToCats.containsKey(subCategoryId)) {
                        subcatToCats.put(subCategoryId, new ArrayList<Long>());
                    }
                    catToSubcats.get(categoryId).add(subCategoryId);
                    subcatToCats.get(subCategoryId).add(categoryId);
                }
            }
        }
    }

    public List<Long> getSubCats(long id) {
        return catToSubcats.get(id);
    }

    public List<Long> getSuperCats(long id) {
        return subcatToCats.get(id);
    }
}
