package ru.abishev.wiki.pages;

import ru.abishev.utils.CsvUtils;
import ru.abishev.wiki.parser.WikiTextParser;
import ru.abishev.wiki.model.AnchorsStat;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class PagesAnalyser2 {
    public static void analyseDump(File anchorsCsv, File statOutput) throws Exception {
        AnchorsStat stat = new AnchorsStat();

        int num = 0;

        for (List<String> anchor : CsvUtils.readCsv(anchorsCsv, '|', '"')) {
            if (anchor.size() != 2) {
                System.out.println(":-( " + anchor);
                continue;
            }

            if (num++ % 2000000 == 0) {
                System.out.println("Analyse " + num + " link");
            }

            stat.addAnchorToStat(anchor.get(0), Integer.parseInt(anchor.get(1)));

            if (stat.getAllWords().size() > 1000000) {
                // compress it!
                stat.compress();
                System.out.println("Size after compression: " + stat.getAllWords().size());
            }
        }

        PrintWriter output = new PrintWriter(statOutput);

        for (String fromWord : stat.getAllWords()) {
            Map<Long, Integer> anchorStat = stat.getAnchorsStat(fromWord).getStat();
            int count = 0;
            for (int _count : anchorStat.values()) {
                count += _count;
            }
            output.print(fromWord + "/" + count + ":");
            for (long pageId : anchorStat.keySet()) {
                output.print(" " + pageId + "/" + anchorStat.get(pageId));
            }
            output.println();
        }
        output.close();
    }

    public static void main(String[] args) throws Exception {
        analyseDump(new File("./data/anchors-normalized.csv"), new File("./data/stat.txt"));
    }
}
