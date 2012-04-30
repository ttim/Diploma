package ru.abishev.wiki.linkifier;

import com.google.common.base.Joiner;

import java.util.*;

public class LinkifierUtils {
    public static Map<String, List<AnchorStatistic>> groupByText(Set<AnchorStatistic> anchors) {
        Map<String, List<AnchorStatistic>> result = new HashMap<String, List<AnchorStatistic>>();
        for (AnchorStatistic anchor : anchors) {
            String text = Joiner.on(" ").join(splitOnPunctuation(anchor.text.toLowerCase()));
            if (!result.containsKey(text)) {
                result.put(text, new ArrayList<AnchorStatistic>());
            }
            result.get(text).add(anchor);
        }
        for (String text : result.keySet()) {
            Collections.sort(result.get(text), new Comparator<AnchorStatistic>() {
                @Override
                public int compare(AnchorStatistic anchor1, AnchorStatistic anchor2) {
                    return anchor2.count - anchor1.count;
                }
            });
        }
        return result;
    }

    public static String[] splitOnPunctuation(String word) {
        return word.split("[ ,.!?:\"\\(\\)\\|/=\\[\\]#{}';<>-]");
    }
}
