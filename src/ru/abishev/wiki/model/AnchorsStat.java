package ru.abishev.wiki.model;

import org.jetbrains.annotations.Nullable;
import sun.security.provider.certpath.CollectionCertStore;

import java.util.*;

public class AnchorsStat {
    public static final int NO_PAGE_ID = -1;

    private Map<String, AnchorStat> stats = new HashMap<String, AnchorStat>();

    public AnchorsStat() {
    }

    public void addAnchorToStat(String fromWord, long toPageId) {
        if (!stats.containsKey(fromWord)) {
            stats.put(fromWord, new AnchorStat(fromWord));
        }
        stats.get(fromWord).addStat(toPageId);
    }

    public Set<String> getAllWords() {
        return stats.keySet();
    }

    @Nullable
    public AnchorStat getAnchorsStat(String fromWord) {
        // todo: copy on return?
        return stats.get(fromWord);
    }

    public static class AnchorStat {
        private final String word;
        private List<Long> pages = new ArrayList<Long>();
        private List<Integer> counts = new ArrayList<Integer>();

        AnchorStat(String word) {
            this.word = word;
        }

        void addStat(long pageId) {
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

        public Map<Long, Integer> getStat() {
            Map<Long, Integer> stat = new HashMap<Long, Integer>();
            for (int i = 0; i < pages.size(); i++) {
                stat.put(pages.get(i), counts.get(i));
            }
            return stat;
        }
    }
}
