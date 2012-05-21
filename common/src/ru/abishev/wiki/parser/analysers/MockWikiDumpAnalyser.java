package ru.abishev.wiki.parser.analysers;

import ru.abishev.wiki.parser.WikiDumpAnalyser;
import ru.abishev.wiki.parser.WikiPage;

public class MockWikiDumpAnalyser implements WikiDumpAnalyser {
    @Override
    public void start() {
        System.out.println("Mock start");
    }

    @Override
    public void analysePage(WikiPage page) {
        System.out.println("Mock analyse: " + page);
    }

    @Override
    public void finish() {
        System.out.println("Mock finish");
    }
}
