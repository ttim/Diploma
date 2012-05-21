package ru.abishev.wiki.parser.analysers;

import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;

public class CounterWikiDumpAnalyser implements WikiDumpAnalyser {
    private int count = 0;

    @Override
    public void start() {
        count = 0;
    }

    @Override
    public void analysePage(WikiPage page) {
        count++;
    }

    @Override
    public void finish() {
        System.out.println("Articles count: " + count);
    }
}
