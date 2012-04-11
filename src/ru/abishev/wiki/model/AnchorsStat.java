package ru.abishev.wiki.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnchorsStat {
    public static final int NO_PAGE_ID = -1;

    private Map<String, AnchorStat> stats = new HashMap<String, AnchorStat>();

    public AnchorsStat() {
    }

    public void addAnchorToStat(String fromWord, int toPageId) {
        if (!stats.containsKey(fromWord)) {
            stats.put(fromWord, new AnchorStat(fromWord));
        }
        stats.get(fromWord).addStat(toPageId);
    }

    @Nullable
    public AnchorStat getAnchorsStat(String fromWord) {
        // todo: copy on return?
        return stats.get(fromWord);
    }

    public static class AnchorStat {
        private final String word;
        private List<Integer> pages = new ArrayList<Integer>();
        private List<Integer> counts = new ArrayList<Integer>();

        AnchorStat(String word) {
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
