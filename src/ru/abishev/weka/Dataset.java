package ru.abishev.weka;

import java.io.File;

public class Dataset {
    private final String datasetName;

    public Dataset(String datasetName) {
        this.datasetName = datasetName;
    }

    public DatasetUsage useFor(String tagName, double trainRatio, double testRatio) {
        return new DatasetUsage(tagName, trainRatio, testRatio);
    }

    public String getDatasetName() {
        return datasetName;
    }

    public class DatasetUsage {
        private final String tagName;
        private final double trainRatio;
        private final double testRatio;

        DatasetUsage(String tagName, double trainRatio, double testRatio) {
            assert trainRatio + testRatio <= 1.0;

            this.tagName = tagName;
            this.trainRatio = trainRatio;
            this.testRatio = testRatio;
        }

        public File getTweetsFile() {
            return new File("./train/" + datasetName + "/" + tagName);

        }

        public String getTagName() {
            return tagName;
        }

        public double getTrainRatio() {
            return trainRatio;
        }

        public double getTestRatio() {
            return testRatio;
        }
    }
}
