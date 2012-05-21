package ru.abishev.wiki.parser;

public interface WikiDumpAnalyser {
    void start() throws Exception;

    void analysePage(WikiPage page) throws Exception;

    void finish() throws Exception;
}
