package ru.abishev.weka.impl;

import ru.abishev.weka.api.Dataset;

import java.io.File;

public class Datasets {
    public static final Dataset FIRST_CROSS = getCrossDataset("thematic");
    public static final Dataset FIRST_TRAINTEST = getTrainTestDataset("thematic");

    public static final Dataset SECOND_CROSS = getCrossDataset("usernewscompany");
    public static final Dataset SECOND_TRAINTEST = getTrainTestDataset("usernewscompany");

    private static Dataset getCrossDataset(String name) {
        return new Dataset.CrossValidationDataset(name + "-cross", new File("./train/" + name + ".train.arff"), 4);
    }

    private static Dataset getTrainTestDataset(String name) {
        return new Dataset.TrainTestDataset(name + "-traintest", new File("./train/" + name + ".train.arff"), new File("./train/" + name + ".test.arff"));
    }
}
