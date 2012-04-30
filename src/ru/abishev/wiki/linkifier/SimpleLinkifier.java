package ru.abishev.wiki.linkifier;

import com.google.common.base.Joiner;
import ru.abishev.Pathes;

import java.io.File;
import java.util.*;

import static ru.abishev.wiki.linkifier.LinkifierUtils.splitOnPunctuation;

public class SimpleLinkifier implements Linkifier {
    public static final SimpleLinkifier INSTANCE = new SimpleLinkifier(Pathes.WIKI_NORMALIZED_ANCHORS_STAT);

    private final AnchorStatistic[] anchors;

    private final int[] next;
    private final int[] anchorId;
    private final Map<String, Integer> first;
    private int wordsCount;

    public SimpleLinkifier(File normalizedAnchorsStat) {
        anchors = AnchorStatistic.readStatistics(normalizedAnchorsStat);
        // lower case word -> indexes

        next = new int[anchors.length * 3];
        anchorId = new int[anchors.length * 3];
        first = new HashMap<String, Integer>();
        wordsCount = 0;

        int num = 0;
        for (AnchorStatistic anchor : anchors) {
            for (String word : splitOnPunctuation(anchor.text.toLowerCase())) {
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

    public Set<AnchorStatistic> linkify(String text) {
        Set<AnchorStatistic> result = new HashSet<AnchorStatistic>();
        List<String> words = Arrays.asList(splitOnPunctuation(text.toLowerCase()));
        String checkWord = " " + Joiner.on(" ").join(words) + " ";
        for (String word : words) {
            if (first.containsKey(word)) {
                int num = first.get(word);
                while (num != 0) {
                    String anchorText = " " + Joiner.on(" ").join(splitOnPunctuation(anchors[anchorId[num]].text.toLowerCase())) + " ";
                    if (checkWord.contains(anchorText) && anchorText.length() > 4) {
                        result.add(anchors[anchorId[num]]);
                    }
                    num = next[num];
                }
            }
        }
        return result;
    }
}
