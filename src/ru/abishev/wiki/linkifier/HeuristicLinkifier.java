package ru.abishev.wiki.linkifier;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HeuristicLinkifier implements Linkifier {
    private final int MAX_COUNT_FROM_ONE_WORD = 1;
    private final double MIN_PERCENT_FROM_FIRST = 0.2;
    private final Linkifier inner;

    public HeuristicLinkifier(Linkifier inner) {
        this.inner = inner;
    }

    @Override
    public Set<AnchorStatistic> linkify(String text) {
        Map<String, List<AnchorStatistic>> innerResult = LinkifierUtils.groupByText(inner.linkify(text));

        Set<AnchorStatistic> result = new HashSet<AnchorStatistic>();

        for (String word : innerResult.keySet()) {
            for (int i = 0; i < Math.min(MAX_COUNT_FROM_ONE_WORD, innerResult.get(word).size()); i++) {
                if (innerResult.get(word).get(i).count * 1.0 / innerResult.get(word).get(0).count < MIN_PERCENT_FROM_FIRST) {
                    break;
                }
                result.add(innerResult.get(word).get(i));
            }
        }

        return result;
    }
}
