package ru.abishev.wiki.parser;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for parsing wikipedia articles text, based on {@link edu.jhu.nlp.wikipedia.WikiTextParser}
 * This class without any kind of caching, be aware, it's just only parsing!
 */
public class WikiTextParser {
    private static final Pattern REDIRECT_PATTERN = Pattern.compile("#REDIRECT\\s+\\[\\[(.*?)\\]\\]", Pattern.CASE_INSENSITIVE);
    private static Pattern STUB_PATTERN = Pattern.compile("\\-stub\\}\\}");
    private static Pattern DISAMBIGUATION_PAGE_PATTERN = Pattern.compile("\\{\\{disambig\\}\\}");
    private static Pattern CATEGORY_PATTERN = Pattern.compile("\\[\\[Category:(.*?)\\]\\]", Pattern.MULTILINE);
    private static Pattern LINKS_PATTERN = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);

    private final String wikiText;

    public WikiTextParser(String wikiText) {
        this.wikiText = wikiText;
    }

    public String getWikiText() {
        return wikiText;
    }

    public boolean isStub() {
        return STUB_PATTERN.matcher(wikiText).find();
    }

    public boolean isDisambiguationPage() {
        return DISAMBIGUATION_PAGE_PATTERN.matcher(wikiText).find();
    }

    @Nullable
    public String parseRedirectText() {
        Matcher matcher = REDIRECT_PATTERN.matcher(wikiText);
        if (matcher.find()) {
            if (matcher.groupCount() == 1) {
                return matcher.group(1);
            } else {
                throw new RuntimeException();
            }
        }
        return null;
    }

    public List<String> parseCategories() {
        List<String> pageCats = new ArrayList<String>();
        Matcher matcher = CATEGORY_PATTERN.matcher(wikiText);
        while (matcher.find()) {
            String[] temp = matcher.group(1).split("\\|");
            pageCats.add(temp[0]);
        }
        return pageCats;
    }

    public List<Link> parseLinks() {
        List<Link> links = new ArrayList<Link>();

        Matcher matcher = LINKS_PATTERN.matcher(wikiText);
        while (matcher.find()) {
            String[] link = matcher.group(1).split("\\|");
            if (link == null || link.length == 0 || link[0].contains(":")) {
                continue;
            }
            if (link.length == 1) {
                links.add(new Link(link[0], link[0]));
            } else if (link.length == 2) {
                links.add(new Link(link[1], link[0]));
            }
        }

        return links;
    }

    public String parsePlainText() {
        String text = wikiText.replaceAll("&gt;", ">");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("<ref>.*?</ref>", " ");
        text = text.replaceAll("</?.*?>", " ");
        text = text.replaceAll("\\{\\{.*?\\}\\}", " ");
        text = text.replaceAll("\\[\\[.*?:.*?\\]\\]", " ");
        text = text.replaceAll("\\[\\[(.*?)\\]\\]", "$1");
        text = text.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
        text = text.replaceAll("\\[.*?\\]", " ");
        text = text.replaceAll("\\'+", "");
        return text;
    }

    public InfoBox parseInfoBox() {
        String INFOBOX_CONST_STR = "{{Infobox";
        int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
        if (startPos < 0) return null;
        int bracketCount = 2;
        int endPos = startPos + INFOBOX_CONST_STR.length();
        for (; endPos < wikiText.length(); endPos++) {
            switch (wikiText.charAt(endPos)) {
                case '}':
                    bracketCount--;
                    break;
                case '{':
                    bracketCount++;
                    break;
                default:
            }
            if (bracketCount == 0) break;
        }
        if (endPos + 1 >= wikiText.length()) return null;
        // This happens due to malformed Infoboxes in wiki text. See Issue #10
        // Giving up parsing is the easier thing to do.
        String infoBoxText = wikiText.substring(startPos, endPos + 1);
        infoBoxText = stripCite(infoBoxText); // strip clumsy {{cite}} tags
        // strip any html formatting
        infoBoxText = infoBoxText.replaceAll("&gt;", ">");
        infoBoxText = infoBoxText.replaceAll("&lt;", "<");
        infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
        infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
        return new InfoBox(infoBoxText);
    }

    private String stripCite(String text) {
        String CITE_CONST_STR = "{{cite";
        int startPos = text.indexOf(CITE_CONST_STR);
        if (startPos < 0) return text;
        int bracketCount = 2;
        int endPos = startPos + CITE_CONST_STR.length();
        for (; endPos < text.length(); endPos++) {
            switch (text.charAt(endPos)) {
                case '}':
                    bracketCount--;
                    break;
                case '{':
                    bracketCount++;
                    break;
                default:
            }
            if (bracketCount == 0) break;
        }
        text = text.substring(0, startPos - 1) + text.substring(endPos);
        return stripCite(text);
    }

    public String parseTranslatedTitle(String languageCode) {
        Pattern pattern = Pattern.compile("^\\[\\[" + languageCode + ":(.*?)\\]\\]$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(wikiText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static class Link {
        public final String text;
        public final String page;

        public Link(String text, String page) {
            this.text = text;
            this.page = page;
        }

        @Override
        public String toString() {
            return "Link{" +
                    "text='" + text + '\'' +
                    ", page='" + page + '\'' +
                    '}';
        }
    }

    public static class InfoBox {
        private final String infoBoxWikiText;

        InfoBox(String infoBoxWikiText) {
            this.infoBoxWikiText = infoBoxWikiText;
        }

        public String dumpRaw() {
            return infoBoxWikiText;
        }
    }
}
