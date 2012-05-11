package ru.abishev.weka;

import com.google.common.base.Joiner;
import ru.abishev.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SimpleDataPreparer {
    public static void prepareData(List<Dataset.DatasetUsage> datasetUsages, File outputFile) throws FileNotFoundException {
        PrintWriter output = new PrintWriter(outputFile);

        output.println("@relation tweet-to-categories");
        output.println("@attribute text string");

        Set<String> categories = new HashSet<String>();
        for (Dataset.DatasetUsage usage : datasetUsages) {
            categories.add(usage.getTagName());
        }
        output.println("@attribute category {" + Joiner.on(',').join(categories) + "}");

        output.println("@data");
        for (Dataset.DatasetUsage usage : datasetUsages) {
            Scanner input = new Scanner(usage.getTweetsFile());

            while (input.hasNextLine()) {
                String line = input.nextLine();
                line = line.substring(0, line.lastIndexOf('\t')).trim();
                // preprocess here? like removing replies etc?
                line = line.replaceAll("'", "");
                output.println("'" + line + "', " + usage.getTagName());
            }

            input.close();
        }

        output.close();
    }

    public static void main(String[] args) throws FileNotFoundException {
        prepareData(Datasets.NEIL_ROBBINSON_80_ON_20_DATASET, new File("./train/output.arff"));
    }
}
