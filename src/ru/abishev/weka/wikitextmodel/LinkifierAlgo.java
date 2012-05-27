package ru.abishev.weka.wikitextmodel;

import com.google.common.base.Joiner;
import ru.abishev.Pathes;
import ru.abishev.wiki.categories.PageRetriever;
import ru.abishev.wiki.categories.data.Pages;
import ru.abishev.wiki.linkifier.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;

import static ru.abishev.wiki.linkifier.LinkifierUtils.splitOnPunctuation;

public class LinkifierAlgo {
    private static LinkifierAlgo INSTANCE = new LinkifierAlgo();
    private static PageRetriever PAGE_RETRIEVER = new PageRetriever(Pathes.PAGES_DB);

    private final int COMMON_WORDS_COUNT = 10;

    private final AnchorStatistic[] anchors;

    private final int[] next;
    private final int[] anchorId;
    private final Map<String, Integer> first;
    private int wordsCount;

    private Set<String> wordsToFilter;

    private void initStopWords() {
        try {
            this.wordsToFilter = new HashSet<String>();

            Scanner input = new Scanner(Pathes.WIKI_ALL_WORDS_STAT);
            for (int i = 0; i < COMMON_WORDS_COUNT; i++) {
                String line = input.nextLine();
                wordsToFilter.add(line.substring(line.indexOf("|") + 1).toLowerCase());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    LinkifierAlgo() {
        initStopWords();

        anchors = AnchorStatistic.readStatistics(Pathes.WIKI_NORMALIZED_ANCHORS_STAT);
        // lower case word -> indexes

        next = new int[anchors.length * 3];
        anchorId = new int[anchors.length * 3];
        first = new HashMap<String, Integer>();
        wordsCount = 0;

        int num = 0;
        for (AnchorStatistic anchor : anchors) {
            for (String word : new HashSet<String>(Arrays.asList(splitOnPunctuation(anchor.text.toLowerCase())))) {
                ++wordsCount;
                anchorId[wordsCount] = num;
                if (first.containsKey(word)) {
                    next[wordsCount] = first.get(word);
                }
                first.put(word, wordsCount);
            }
            num++;
        }
    }

    public static Map<Integer, Double> linkify(String text) {
        return INSTANCE.innerLinkify(text);
    }

    private ArrayList<AnchorStatistic> collectForWord(String word) {
        ArrayList<AnchorStatistic> anchors = new ArrayList<AnchorStatistic>();

        if (!first.containsKey(word) || word == null || word.length() < 4 || wordsToFilter.contains(word.toLowerCase())) {
            return anchors;
        }

        int num = first.get(word);
        while (num != 0) {
            AnchorStatistic stat = this.anchors[anchorId[num]];
            anchors.add(stat);
            num = next[num];
        }

        // reverse
        ArrayList<AnchorStatistic> reversed = new ArrayList<AnchorStatistic>();
        for (int i = anchors.size() - 1; i >= 0; i--) {
            reversed.add(anchors.get(i));
        }

        return reversed;
    }

    private Map<Integer, Double> innerLinkify(String text) {
        // 0 - extracting terms
        Set<String> terms = new HashSet<String>();
        Set<String> words = new HashSet<String>(Arrays.asList(splitOnPunctuation(text.toLowerCase())));
        // filter stop words
        words.removeAll(wordsToFilter);

        String checkString = " " + Joiner.on(' ').join(Arrays.asList(splitOnPunctuation(text.toLowerCase()))) + " ";

        // prepare AnchorStatistic -> normalizedTitle
        Set<AnchorStatistic> anchors = new HashSet<AnchorStatistic>();
        for (String word : words) {
            for (AnchorStatistic statistic : collectForWord(word)) {
                boolean ok = true;
                for (String _word : splitOnPunctuation(statistic.text.toLowerCase())) {
                    if (!words.contains(_word) && !wordsToFilter.contains(_word)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    String term = " " + Joiner.on(' ').join(splitOnPunctuation(statistic.text.toLowerCase())) + " ";
                    if (checkString.contains(term)) {
                        terms.add(term);
                        anchors.add(statistic);
                    }
                }
            }
        }

        // update terms
        Set<String> toDelete = new HashSet<String>();
        for (String term : terms) {
            boolean isOk = true;
            for (String term2 : terms) {
                if (term2.length() > term.length() && term2.contains(term)) {
                    isOk = false;
                    break;
                }
            }
            if (!isOk) {
                toDelete.add(term);
            }
        }
        terms.removeAll(toDelete);

        // 1 - linking probability
        Map<Integer, Double> linkingProbability = new HashMap<Integer, Double>();

        for (String term : terms) {
            Map<Integer, Integer> pageIdToCount = new HashMap<Integer, Integer>();
            int curCount = 0;

            for (AnchorStatistic stat : anchors) {
                String title = " " + Joiner.on(' ').join(splitOnPunctuation(stat.text.toLowerCase())) + " ";
                if (title.equals(term)) {
                    curCount += stat.count;
                    if (pageIdToCount.containsKey(stat.pageId)) {
                        pageIdToCount.put(stat.pageId, pageIdToCount.get(stat.pageId) + stat.count);
                    } else {
                        pageIdToCount.put(stat.pageId, stat.count);
                    }
                }
            }

            if (curCount < 20) {
                continue;
            }
            if (curCount < 30 && pageIdToCount.size() == 1) {
                continue;
            }

            for (int pageId : pageIdToCount.keySet()) {
                double p = pageIdToCount.get(pageId) * 1.0 / curCount;
                if (p > 0.05) {
                    if (!linkingProbability.containsKey(pageId) || (p > linkingProbability.get(pageId))) {
                        linkingProbability.put(pageId, p);
                    }
                }
            }
        }

        // 2 - overlapping ratio
        Map<Integer, Double> overlap = new HashMap<Integer, Double>();
        for (int pageId : linkingProbability.keySet()) {
            String page = "";
            try {
                page = PAGE_RETRIEVER.getContent(pageId);
            } catch (SQLException e) {
                // ...
            }
            int count = 0;
            for (String word : words) {
                if (page.contains(word)) {
                    count++;
                }
            }
            overlap.put(pageId, count * 1.0 / words.size());
        }

        // 3 - result
        Map<Integer, Double> pages = new HashMap<Integer, Double>();
        for (int id : linkingProbability.keySet()) {
            double rate = ((overlap.containsKey(id) ? overlap.get(id) : 0) + (linkingProbability.containsKey(id) ? linkingProbability.get(id) : 0)) / 2;
            if (rate > 0.05) {
                pages.put(id, rate);
            }
        }

        return pages;
    }

    private static List<Map.Entry<Integer, Double>> getSortedEntries(String text) {
        List<Map.Entry<Integer, Double>> entries = new ArrayList<Map.Entry<Integer, Double>>(linkify(text).entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return Double.compare(o2.getValue(), o1.getValue());
            }
        });
        return entries;
    }

    private static void test(String text) {
        System.out.println("linkify for " + text);
        for (Map.Entry<Integer, Double> entry : getSortedEntries(text)) {
            System.out.printf("%s -> %.2f, ", Pages.INSTANCE.getById(entry.getKey()).title, entry.getValue());
        }
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        test("China, France cautious on nuke energy: PARIS (AP) - Japan's nuclear crisis reverberated in atomic power-friendly");
    }

    public static Set<Integer> linkifyWithLimit(String text) {
        Set<Integer> result = new HashSet<Integer>();

        int num = 0;
        for (Map.Entry<Integer, Double> entry : getSortedEntries(text)) {
            if (entry.getValue() >= 0.35 && num <= 5) {
                result.add(entry.getKey());
            }
        }

        if (WikiTextModel.debug) {
//            System.out.print("pages");
//            for (int id : result) {
//                System.out.print(" " + Pages.INSTANCE.getById(id).title);
//            }
        }

        return result;
    }
}
