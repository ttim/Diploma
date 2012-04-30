package ru.abishev.wiki.linkifier;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static ru.abishev.wiki.linkifier.LinkifierUtils.groupByText;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        String text = "Nice source of Ruby-relevant Smalltalk articles: The Smalltalk Report";

        Linkifier linkifier = new HeuristicLinkifier(new FilteringLinkifier(SimpleLinkifier.INSTANCE));

        for (Map.Entry<String, List<AnchorStatistic>> entry : groupByText(linkifier.linkify(text)).entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
