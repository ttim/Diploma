package ru.abishev.wiki.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordsStat {
    public static final int NO_PAGE_ID = -1;



    public WordsStat() {
    }

    public void addWordToStat(String word, int pageId) {

    }

    public WordStat getWordStat(String word) {
        // todo: should be immutable
        return null;
    }

    public static class WordStat {
        private final String word;
        private List<Integer> pages = new ArrayList<Integer>();
        private List<Integer> counts = new ArrayList<Integer>();

        WordStat(String word) {
            this.word = word;
        }

        void addStat(int pageId) {
            for (int i = 0; i < pages.size(); i++) {
                if (pages.get(i) == pageId) {
                    counts.set(i, counts.get(i) + 1);
                    break;
                }
            }
            pages.add(pageId);
            counts.add(1);
            // maybe optimize order
            // maybe use hash map
        }

        public String getWord() {
            return word;
        }

        public Map<Integer, Integer> getStat() {
            Map<Integer, Integer> stat = new HashMap<Integer, Integer>();
            for (int i = 0; i < pages.size(); i++) {
                stat.put(pages.get(i), counts.get(i));
            }
            return stat;
        }
    }
}
