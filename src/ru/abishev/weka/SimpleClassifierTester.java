package ru.abishev.weka;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;

import java.io.*;

public class SimpleClassifierTester {
    public static Classifier getSimpleClassifier(String classifierName) {
        Classifier classifier = (Classifier) WekaUtils.readObjectFromFile(new File("./weka/classifiers/" + classifierName));

        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(classifier);
        filteredClassifier.setFilter(WekaUtils.getRemovingFilter());

        return filteredClassifier;
//        return classifier;
    }

    public static void evalForClassifier(String classifierName, File train, File test, Filter stringToVector, String textModelPrintName) throws Exception {
        ClassifierTesterUtils.evalForClassifier("simple / " + textModelPrintName + " / " + classifierName, getSimpleClassifier(classifierName), train, test, stringToVector);
    }

    public static void evalForFiles(File train, File test, Filter stringToVector, String textModelPrintName) throws Exception {
        evalForClassifier("naivebayes", train, test, stringToVector, textModelPrintName);
        evalForClassifier("svm", train, test, stringToVector, textModelPrintName);
        evalForClassifier("j48", train, test, stringToVector, textModelPrintName);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("precision\trecall\tfmeasure");

        System.out.println("dataset1");
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR, "simple-text-model");
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"), ClassifierTesterUtils.WIKI_STRING_TO_VECTOR, "wiki-text-model");

        System.out.println();

        System.out.println("dataset2");
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR, "simple-text-model");
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"), ClassifierTesterUtils.WIKI_STRING_TO_VECTOR, "wiki-text-model");
    }
}
