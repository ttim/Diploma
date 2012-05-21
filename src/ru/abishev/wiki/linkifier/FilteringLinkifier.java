package ru.abishev.wiki.linkifier;

import ru.abishev.Pathes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FilteringLinkifier implements Linkifier {
    private final int COMMON_WORDS_COUNT = 10;

    private final Linkifier inner;
    private final Set<String> wordsToFilter;

    public FilteringLinkifier(Linkifier inner) throws FileNotFoundException {
        this.inner = inner;
        this.wordsToFilter = new HashSet<String>();

        Scanner input = new Scanner(Pathes.WIKI_ALL_WORDS_STAT);
        for (int i = 0; i < COMMON_WORDS_COUNT; i++) {
            String line = input.nextLine();
            wordsToFilter.add(line.substring(line.indexOf("|") + 1).toLowerCase());
        }
    }

    @Override
    public Set<AnchorStatistic> linkify(String text) {
        Set<AnchorStatistic> result = new HashSet<AnchorStatistic>();
        for (AnchorStatistic anchor : inner.linkify(text)) {
            if (!wordsToFilter.contains(anchor.text.toLowerCase())) {
                result.add(anchor);
            }
        }
        return result;
    }
}
