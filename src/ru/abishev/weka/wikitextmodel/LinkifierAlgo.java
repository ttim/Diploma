package ru.abishev.weka.wikitextmodel;

import ru.abishev.wiki.linkifier.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public class LinkifierAlgo {
    private static Linkifier linkifier;

    static {
        try {
            linkifier = new HeuristicLinkifier(new FilteringLinkifier(SimpleLinkifier.INSTANCE));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Integer> linkify(String text) {
        return oldLinkify(text);
    }

    public static Set<Integer> oldLinkify(String text) {
        Set<Integer> pages = new HashSet<Integer>();
        for (AnchorStatistic stat : linkifier.linkify(text)) {
            pages.add(stat.pageId);
        }
        return pages;
    }
}
