package ru.abishev.wiki.linkifier;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static ru.abishev.wiki.linkifier.LinkifierUtils.groupByText;

public class Test {
    public static Linkifier linkifier;

    static {
        try {
            linkifier = new HeuristicLinkifier(new FilteringLinkifier(SimpleLinkifier.INSTANCE));
        } catch (FileNotFoundException e) {
        }
    }

    public static void testText(String text) {
        System.out.println("Result for " + text);
        for (Map.Entry<String, List<AnchorStatistic>> entry : groupByText(linkifier.linkify(text)).entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        testText("Nice source of Ruby-relevant Smalltalk articles: The Smalltalk Report");

//        for (Status s : Tweets.DHH_200_TWEETS) {
//            testText(s.getText());
//            System.out.println();
//        }
    }
}
