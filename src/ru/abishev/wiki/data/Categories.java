package ru.abishev.wiki.data;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.*;

public class Categories implements Iterable<Category> {
    public static Categories RAW = new Categories(new File("./data/preprocessed/category.csv"));

    private List<Category> categories;

    private Map<String, Long> nameToIdMap;
    private Map<Long, Category> idToCategoryMap;
    private Map<Long, Category> pageIdToCategory;

    public Categories(File categoriesCsv) {
        System.out.println("Init from " + categoriesCsv);

        categories = new ArrayList<Category>();
        nameToIdMap = new HashMap<String, Long>();
        idToCategoryMap = new HashMap<Long, Category>();
        pageIdToCategory = new HashMap<Long, Category>();

        for (List<String> data : CsvUtils.readCsvIgnoreFirstLine(categoriesCsv, '|', '\'')) {
            if (data.size() != 6) {
                throw new RuntimeException(data.toString());
            }

            String name = data.get(1);
            name = name.substring(1, name.length() - 1);
            categories.add(new Category(Long.parseLong(data.get(0)), name, Integer.parseInt(data.get(2)), Integer.parseInt(data.get(3)), Long.parseLong(data.get(5))));
        }

        for (Category category : categories) {
            if (!nameToIdMap.containsKey(category.name)) {
                nameToIdMap.put(category.name, category.id);
            } else {
//                throw new RuntimeException(category.name);
            }
            idToCategoryMap.put(category.id, category);
            pageIdToCategory.put(category.pageId, category);
        }

        System.out.println("Categories size: " + categories.size());
    }


    @Override
    public Iterator<Category> iterator() {
        return categories.iterator(); // todo: should be without remove!
    }

    public Category getById(long id) {
        return idToCategoryMap.get(id);
    }

    public Category getByName(String name) {
        return getById(nameToIdMap.get(name));
    }

    public Category getByPageId(long pageId) {
        return pageIdToCategory.get(pageId);
    }

    public boolean containsName(String name) {
        return nameToIdMap.containsKey(name);
    }
}
