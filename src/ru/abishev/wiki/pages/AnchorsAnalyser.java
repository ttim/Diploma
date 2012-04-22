package ru.abishev.wiki.pages;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class AnchorsAnalyser {
    private static void flushTable(Table<String, Integer, Integer> table, PrintWriter output, int limit, boolean printSorted) {
        System.out.println("Current size:" + table.size());

        int writtenCount = 0;

        // printing
        Iterable<Table.Cell<String, Integer, Integer>> cells;
        if (!printSorted) {
            cells = table.cellSet();
        } else {
            List<Table.Cell<String, Integer, Integer>> sortedCells = new ArrayList<Table.Cell<String, Integer, Integer>>(table.cellSet());
            Collections.sort(sortedCells, new Comparator<Table.Cell<String, Integer, Integer>>() {
                @Override
                public int compare(Table.Cell<String, Integer, Integer> cell1, Table.Cell<String, Integer, Integer> cell2) {
                    return cell2.getValue() - cell1.getValue();
                }
            });
            cells = sortedCells;
        }

        for (Table.Cell<String, Integer, Integer> cell : cells) {
            if (cell.getValue() > limit) {
                output.println(cell.getRowKey() + "|" + cell.getColumnKey() + "|" + cell.getValue());
                writtenCount++;
            }
        }

        System.out.println("Written count: " + writtenCount);

        table.clear();
    }

    public static void analyseDump(File anchorsCsv, File statOutput, int limit, boolean printSorted) throws Exception {
        Table<String, Integer, Integer> anchorAndPageToCount = HashBasedTable.create();
        int num = 0;

        PrintWriter output = new PrintWriter(statOutput);

        for (List<String> anchor : CsvUtils.readCsv(anchorsCsv, '|', '"')) {
            if (num++ % 15000000 == 0) {
                System.out.println("Dump statistic at " + num + " count");
                flushTable(anchorAndPageToCount, output, limit, printSorted);
            }

            if (anchor.size() != 2 && anchor.size() != 3) {
                System.out.println(":-( " + anchor);
                continue;
            }

            String anchorText = anchor.get(0);
            int pageId = Integer.parseInt(anchor.get(1));
            int count = anchor.size() == 3 ? Integer.parseInt(anchor.get(2)) : 1;

            Integer currentCount = anchorAndPageToCount.get(anchorText, pageId);
            if (currentCount == null) {
                currentCount = 0;
            }

            anchorAndPageToCount.put(anchorText, pageId, currentCount + count);
        }

        System.out.println("Dump stat at the end");
        flushTable(anchorAndPageToCount, output, limit, printSorted);

        output.close();
    }

    public static void main(String[] args) throws Exception {
//        analyseDump(new File("./data/anchors-normalized.csv"), new File("./data/tmp1.txt"), 0, false);
//        analyseDump(new File("./data/tmp1.txt"), new File("./data/tmp2.txt"), 0, false);
//        analyseDump(new File("./data/tmp2.txt"), new File("./data/tmp3.txt"), 0, false);
//        analyseDump(new File("./data/tmp3.txt"), new File("./data/stat.txt"), 0, false);

//        analyseDump(new File("./data/stat.txt"), new File("./data/stat_5.txt"), 5, false);
        analyseDump(new File("./data/stat_5.txt"), new File("./data/stat_5_sorted.txt"), 5, true);
    }
}
