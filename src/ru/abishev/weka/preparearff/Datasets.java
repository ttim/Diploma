package ru.abishev.weka.preparearff;

import java.util.Arrays;
import java.util.List;

public class Datasets {
    public static Dataset DATASET1 = new Dataset("set1");
    public static Dataset DATASET2 = new Dataset("set2");
    public static Dataset DATASET3 = new Dataset("set3");
    public static Dataset DATASET4 = new Dataset("set4");
    public static Dataset DATASET5 = new Dataset("set5");

    public static List<Dataset.DatasetUsage> NEIL_ROBBINSON_80_ON_20_DATASET = Arrays.asList(
            DATASET1.useFor("culture", 0.8, 0.2),
            DATASET1.useFor("life", 0.8, 0.2),
            DATASET1.useFor("technology", 0.8, 0.2)
    );
}
