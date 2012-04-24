package ru.abishev.utils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;

import static ru.abishev.utils.FileUtils.readFileAsChunks;

public class StatisticCreator {
    private static void flushStatistic(Map<String, Integer> statistic, PrintWriter output, int limit, boolean printSorted) {
        System.out.println("Current size:" + statistic.size());

        int writtenCount = 0;

        // printing
        Iterable<Map.Entry<String, Integer>> entries;
        if (!printSorted) {
            entries = statistic.entrySet();
        } else {
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(statistic.entrySet());
            Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                    return entry2.getValue() - entry1.getValue();
                }
            });
            entries = sortedEntries;
        }

        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue() > limit) {
                output.println(entry.getValue() + "|" + entry.getKey());
                writtenCount++;
            }
        }

        System.out.println("Written count: " + writtenCount);

        statistic.clear();
    }

    @Nullable
    private static Integer tryParse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void processStatistic(File data, File outputFile, int limit, boolean printSorted) throws Exception {
        Map<String, Integer> statistic = new HashMap<String, Integer>();
        int num = 0;

        PrintWriter output = new PrintWriter(outputFile);

        for (FileUtils.Chunk chunk : readFileAsChunks(data, Charset.forName("UTF-8"), 1000000)) {
            if (chunk.isContinue) {
                throw new RuntimeException();
            }

            num++;
            if (statistic.size() > 3000000) {
                System.out.println("Dump statistic at " + num + " count");
                flushStatistic(statistic, output, limit, printSorted);
            }

            String line = chunk.data.trim();

            String currentString;
            int currentCount;

            if (line.contains("|") && (tryParse(line.substring(0, line.indexOf("|"))) != null)) {
                currentString = line.substring(line.indexOf("|") + 1);
                currentCount = tryParse(line.substring(0, line.indexOf("|")));
            } else {
                currentString = line;
                currentCount = 1;
            }

            if (statistic.containsKey(currentString)) {
                statistic.put(currentString, statistic.get(currentString) + currentCount);
            } else {
                statistic.put(currentString, currentCount);
            }
        }

        System.out.println("Dump stat at the end");
        flushStatistic(statistic, output, limit, printSorted);

        output.close();
    }

    public static void main(String[] args) throws Exception {
        // todo: do it automatically
//        processStatistic(new File("./data/words.csv"), new File("./data/tmp1.txt"), 0, false);
//        processStatistic(new File("./data/tmp1.txt"), new File("./data/tmp2.txt"), 1, false);
//        processStatistic(new File("./data/tmp2.txt"), new File("./data/tmp3.txt"), 2, false);
//        processStatistic(new File("./data/tmp3.txt"), new File("./data/tmp4.txt"), 5, false);
//        processStatistic(new File("./data/tmp4.txt"), new File("./data/tmp5.txt"), 10, false);
//        processStatistic(new File("./data/tmp5.txt"), new File("./data/tmp7.txt"), 10, false);
//        processStatistic(new File("./data/tmp7.txt"), new File("./data/all-words-stat.txt"), 10, true);
    }
}
