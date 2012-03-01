package ru.abishev.wiki.data;

public class Category {
    public final long id;
    public final String name;
    public final int pagesCount;
    public final int subCatsCount;
    public final long pageId;

    Category(long id, String name, int pagesCount, int subCatsCount, long pageId) {
        this.id = id;
        this.name = name;
        this.pagesCount = pagesCount;
        this.subCatsCount = subCatsCount;
        this.pageId = pageId;
    }

    @Override
    public String toString() {
        return "Category{" +
                "pageId=" + pageId +
                ", subCatsCount=" + subCatsCount +
                ", pagesCount=" + pagesCount +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
