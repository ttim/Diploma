package ru.abishev.wiki.example2;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgrammingDetector {
    public Map<String, Integer> progStat = new HashMap<String, Integer>();
    public Map<String, Integer> allStat = new HashMap<String, Integer>();

    public ProgrammingDetector(File progAnchorsFile, File allAnchorsStatFile) {
        for (List<String> data : CsvUtils.readCsv(progAnchorsFile, '|', '"')) {
            for (String w : splitOnPunctuation(data.get(0))) {
                addToValue(progStat, w.toLowerCase(), 1);
            }
        }
        System.out.println("Words loaded (prog): " + progStat.size());

        for (List<String> data : CsvUtils.readCsv(allAnchorsStatFile, '|', '"')) {
            int count = Integer.parseInt(data.get(2));
            for (String w : splitOnPunctuation(data.get(0))) {
                addToValue(allStat, w.toLowerCase(), count);
            }
        }
        System.out.println("Words loaded (all): " + allStat.size());
    }

    private static <K> void addToValue(Map<K, Integer> map, K key, int addition) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + addition);
        } else {
            map.put(key, addition);
        }
    }

    public double rate(String s) {
        double count = 0;
        for (String w : splitOnPunctuation(s)) {
            if (w.length() > 3 && progStat.containsKey(w.toLowerCase())) {
                if (!allStat.containsKey(w.toLowerCase())) {
                    System.out.println("oO " + w);
                } else {
                    count += progStat.get(w.toLowerCase()) * 1.0 / allStat.get(w.toLowerCase());
                }
            }
        }
        return count;
    }

    private String[] splitOnPunctuation(String w) {
        return w.split("[ ,.!?:\"]");
    }

    public static void main(String[] args) {
        final ProgrammingDetector detector = new ProgrammingDetector(new File("./data/prog_anchors.csv"), new File("./data/stat_5.txt"));

        System.out.println(detector.rate("Clippy for Java Users: \"It appears you are trying to download the entire internet. Would you like help building your Java Project?\""));
        System.out.println(detector.rate("No our language is not OO even though its based on #Ruby. That's like saying a language must be imperative if it is implemented in C"));
    }
}
