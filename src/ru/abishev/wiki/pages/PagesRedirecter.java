package ru.abishev.wiki.pages;

import ru.abishev.persistentobjects.client.PersistentObjects;
import ru.abishev.utils.CsvUtils;
import ru.abishev.utils.StringPool;

import java.io.File;
import java.util.List;

public class PagesRedirecter {
    private Redirecter redirecter;

    public PagesRedirecter(String pagesIndexFile, boolean isRemote) {
        redirecter = PersistentObjects.create(Redirecter.class, RedirecterImpl.class, !isRemote, pagesIndexFile);
    }

    public PageResult redirectPage(String name) {
        Object[] result = redirecter.doRequest(name);

        if (result == null) {
            return PageResult.BAD_RESULT;
        } else {
            return new PageResult((Integer) result[0], (String) result[1], (Boolean) result[2], (String) result[3], (Integer) result[4], (String) result[5]);
        }

    }

    public static class PageResult {
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
    }

    public static interface Redirecter {
        Object[] doRequest(String name);
    }

    public static class RedirecterImpl implements Redirecter {
        // todo: read from file index!
        private static final int MAX_TITLES_COUNT = 10500000;
        private static final int MAX_PAGE_ID = 35500000;

        // init this values from file!
        private StringPool pool = new StringPool(MAX_TITLES_COUNT);
        int[] pageIdToTitle = new int[MAX_PAGE_ID];
        int[] titleToPageId = new int[MAX_TITLES_COUNT];
        int[] redirects = new int[MAX_PAGE_ID];

        public RedirecterImpl(String pagesIndexFile) {
            int badCount = 0;
            int badNotRedirectsCount = 0;
            // read pages file
            // two steps
            System.out.println("First step");
            int count = 0;
            for (List<String> page : CsvUtils.readCsv(new File(pagesIndexFile), '|', '"')) {
                if (count++ % 500000 == 0) {
                    System.out.println("Line num: " + count);
                }

                int id = Integer.parseInt(page.get(0));
                String title = page.get(1);
                if (pool.getId(title) != -1) {
                    // ignore for now, but it should not be
                    badCount++;
                    if (page.size() > 2) {
                        badNotRedirectsCount++;
                    }
                }
                int titleId = pool.add(title);

                pageIdToTitle[id] = titleId;
                titleToPageId[titleId] = id;
            }
            System.out.println("Bad titles count: " + badCount + "; not redirects: " + badNotRedirectsCount);

            System.out.println("Second step");
            // analyse redirects
            count = 0;
            int badRedirectsCount = 0, redirectsCount = 0;
            for (List<String> page : CsvUtils.readCsv(new File(pagesIndexFile), '|', '"')) {
                if (count++ % 500000 == 0) {
                    System.out.println("Line num: " + count);
                }

                int id = Integer.parseInt(page.get(0));
                if (page.size() > 2) {
                    String redirectTo = page.get(2);
                    if (redirectTo.length() > 0) {
                        if (redirectTo.contains("#")) {
                            redirectTo = redirectTo.substring(0, redirectTo.indexOf("#"));
                        }
                        redirects[id] = pool.getId(redirectTo);
                        if (redirects[id] == -1) {
//                            if (badRedirectsCount == 100) {
//                                System.out.println(page.get(0));
//                            }
                            badRedirectsCount++;
                        }
                        redirectsCount++;
                    }
                }
            }

            System.out.println("Redirects count: " + redirectsCount + "; bad redirects count: " + badRedirectsCount);
        }

        public Object[] doRequest(String name) {
            // should be with preprocessing...
            return findByName(name);
        }

        Object[] findByName(String name) {
            int titleId = pool.getId(name);
            if (titleId == -1) {
                return null;
            }
            int steps = 0;
            while (redirects[titleToPageId[titleId]] != 0 && (steps++ < 100)) {
                if (redirects[titleToPageId[titleId]] == -1) {
                    return null;
                }
                titleId = redirects[titleToPageId[titleId]];
            }
            if (steps >= 100) {
                System.out.println("Steps >= 100 on " + name);
                return null;
            }
            int firstPageId = titleToPageId[pool.getId(name)];
            boolean isRedirect = redirects[firstPageId] != 0;
            return new Object[] {firstPageId, name, isRedirect, isRedirect ? pool.getById(redirects[firstPageId]) : "", titleToPageId[titleId], pool.getById(titleId)};
        }
    }
}
