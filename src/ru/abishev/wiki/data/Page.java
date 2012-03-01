package ru.abishev.wiki.data;

public class Page {
    public final long id;
    public final int namespace;
    public final String title;
    
    Page(long id, int namespace, String title) {
        this.id = id;
        this.namespace = namespace;
        this.title = title;
    }
}
