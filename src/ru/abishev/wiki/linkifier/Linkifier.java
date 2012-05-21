package ru.abishev.wiki.linkifier;

import java.util.Set;

public interface Linkifier {
    Set<AnchorStatistic> linkify(String text);
}

