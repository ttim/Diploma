package ru.abishev.weka.api;

import java.io.File;

public abstract class Dataset {
    public abstract String getFullName();

    public static class CrossValidationDataset extends Dataset {
        public final String name;

        public final File trainFile;
        public final int foldsCount;

        public CrossValidationDataset(String name, File trainFile, int foldsCount) {
            this.name = name;

            this.trainFile = trainFile;
            this.foldsCount = foldsCount;
        }

        @Override
        public String getFullName() {
            return name;
        }
    }

    public static class TrainTestDataset extends Dataset {
        public final String name;

        public final File trainFile;
        public final File testFile;

        public TrainTestDataset(String name, File trainFile, File testFile) {
            this.name = name;

            this.trainFile = trainFile;
            this.testFile = testFile;
        }

        @Override
        public String getFullName() {
            return name;
        }
    }
}
