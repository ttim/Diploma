package ru.abishev.wiki.pages;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class AnchorsAnalyser {
    private static void flushTable(Table<String, Integer, Integer> table, PrintWriter output, int limit) {
        System.out.println("Current size:" + table.size());

        int writtenCount = 0;

        // printing
        for (Table.Cell<String, Integer, Integer> cell : table.cellSet()) {
            if (cell.getValue() > limit) {
                output.println(cell.getRowKey() + "|" + cell.getColumnKey() + "|" + cell.getValue());
                writtenCount++;
            }
        }

        System.out.println("Written count: " + writtenCount);

        table.clear();
    }

    public static void analyseDump(File anchorsCsv, File statOutput, int limit) throws Exception {
        Table<String, Integer, Integer> anchorAndPageToCount = HashBasedTable.create();
        int num = 0;

        PrintWriter output = new PrintWriter(statOutput);

        for (List<String> anchor : CsvUtils.readCsv(anchorsCsv, '|', '"')) {
            if (num++ % 15000000 == 0) {
                System.out.println("Dump statistic at " + num + " count");
                flushTable(anchorAndPageToCount, output, limit);
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
        flushTable(anchorAndPageToCount, output, limit);

        output.close();
    }

    public static void main(String[] args) throws Exception {
//        analyseDump(new File("./data/anchors-normalized.csv"), new File("./data/tmp1.txt"), 0);
//        analyseDump(new File("./data/tmp1.txt"), new File("./data/tmp2.txt"), 0);
//        analyseDump(new File("./data/tmp2.txt"), new File("./data/tmp3.txt"), 0);
//        analyseDump(new File("./data/tmp3.txt"), new File("./data/stat.txt"), 0);

        analyseDump(new File("./data/stat.txt"), new File("./data/stat_5.txt"), 5);
    }
}
