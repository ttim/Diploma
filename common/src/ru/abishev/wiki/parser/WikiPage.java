package ru.abishev.wiki.parser;

public class WikiPage {
    public final long id;
    public final String title;
    public final String text;

    public WikiPage(long id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return "WikiPage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
