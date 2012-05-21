package ru.abishev.wiki.categories.data;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (id != category.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
