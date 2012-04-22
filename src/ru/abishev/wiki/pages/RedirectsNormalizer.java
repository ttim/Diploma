package ru.abishev.wiki.pages;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.parser.WikiTextParser;

import java.io.*;
import java.util.List;

public class RedirectsNormalizer {
    public static void normalizeAnchors(File anchorsCsv, File anchorsNormalizedCsv) throws Exception {
        PagesRedirecter redirecter = new PagesRedirecter(new File("./data/pages-index.csv").getAbsolutePath(), false);

        PrintWriter output = new PrintWriter(anchorsNormalizedCsv);

        int badCount = 0, goodCount = 0;

        for (List<String> anchor : CsvUtils.readCsv(anchorsCsv, '|', '"')) {
            if (anchor.size() != 2) {
                System.out.println(":-( " + anchor);
                continue;
            }

            WikiTextParser.Link link = new WikiTextParser.Link(anchor.get(0), anchor.get(1));

            PagesRedirecter.PageResult pageResult = redirecter.redirectPage(link.page);
            if (pageResult == null || pageResult.isBad()) {
                if (link.page.length() > 0) {
                    pageResult = redirecter.redirectPage(Character.toTitleCase(link.page.charAt(0)) + link.page.substring(1));
                }
            }

            if (pageResult == null || pageResult.isBad()) {
                badCount++;
            } else {
                goodCount++;
                output.println(link.text + "|" + pageResult.finalPageId);
            }
            if ((badCount + goodCount) % 100000 == 0) {
                System.out.println("Bad count: " + badCount + "; good count: " + goodCount);
            }
        }

        output.close();
    }

    public static void main(String[] args) throws Exception {
        normalizeAnchors(new File("./data/anchors.csv"), new File("./data/anchors-normalized.csv"));
    }
}
