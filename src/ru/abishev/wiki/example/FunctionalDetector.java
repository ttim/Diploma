package ru.abishev.wiki.example;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionalDetector {
    public Set<String> stat = new HashSet<String>();

    public FunctionalDetector(File anchorsFile) {
        for (List<String> data : CsvUtils.readCsv(anchorsFile, '|', '"')) {
            for (String w : splitOnPunctuation(data.get(0))) {
                stat.add(w.toLowerCase());
            }
        }
        System.out.println("Words loaded: " + stat.size());
    }

    public double rate(String s) {
        int count = 0;
        for (String w : splitOnPunctuation(s)) {
            if (stat.contains(w.toLowerCase())) {
                count++;
            }
        }

        return count * 1.0 / splitOnPunctuation(s).length;
    }

    private String[] splitOnPunctuation(String w) {
        return w.split("[ ,.!?:\"]");
    }

    public static void main(String[] args) {
        FunctionalDetector detector = new FunctionalDetector(new File("./data/fprog_anchors.csv"));
        System.out.println(detector.rate("Clippy for Java Users: \"It appears you are trying to download the entire internet. Would you like help building your Java Project?\""));

    }


}
