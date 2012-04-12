package ru.abishev.wiki.pages;

public class PageResult {
    public static final PageResult BAD_RESULT = new PageResult(0, "", false, "", 0, "");

    // 0 in case of nothing
    public long pageId;

    public String title;
    public boolean isRedirect;
    public String redirectTo;

    public long finalPageId;
    public String finalTitle;

    PageResult(long pageId, String title, boolean isRedirect, String redirectTo, long finalPageId, String finalTitle) {
        this.pageId = pageId;
        this.title = title;
        this.isRedirect = isRedirect;
        this.redirectTo = redirectTo;
        this.finalPageId = finalPageId;
        this.finalTitle = finalTitle;
    }

    @Override
    public String toString() {
        // use | as separator
        return pageId + "|" + title + "|" + isRedirect + "|" + redirectTo + "|" + finalPageId + "|" + finalTitle;
    }

    public boolean isBad() {
        return pageId == 0;
    }

    public static PageResult fromString(String data) {
        String[] parsedData = data.split("\\|");
        return new PageResult(
                Long.parseLong(parsedData[0]),
                parsedData[1],
                Boolean.parseBoolean(parsedData[2]),
                parsedData[3],
                Long.parseLong(parsedData[4]),
                parsedData[5]
        );
    }
}
