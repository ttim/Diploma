package ru.abishev.weka;

import ru.abishev.weka.context.ContextClassifier;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.filters.Filter;

import java.io.File;

public class ContextClassifierTester {
    public static Classifier getContextClassifier(String baseClassifierName, String clustererName, Filter stringToVector) {
        Classifier baseClassifier = SimpleClassifierTester.getSimpleClassifier(baseClassifierName);
        Clusterer clusterer = (Clusterer) WekaUtils.readObjectFromFile(new File("./weka/clusterers/" + clustererName));
        return new ContextClassifier(baseClassifier, clusterer, stringToVector);
    }

    public static void evalForClassifier(String baseClassifierName, String clustererName, File train, File test, Filter stringToVector, String textModelPrintName) throws Exception {
        ClassifierTesterUtils.evalForClassifier("context / " + textModelPrintName + " / " + baseClassifierName + "-" + clustererName, getContextClassifier(baseClassifierName, clustererName, stringToVector), train, test, stringToVector);
    }

    public static void evalForFilesAndClusterer(String clustererName, File train, File test, Filter stringToVector, String textModelPrintName) throws Exception {
//        evalForClassifier("naivebayes", clustererName, train, test, stringToVector, textModelPrintName);
//        evalForClassifier("svm", clustererName, train, test, stringToVector, textModelPrintName);
        evalForClassifier("j48", clustererName, train, test, stringToVector, textModelPrintName);
    }

    public static void evalForFiles(File train, File test, Filter stringToVector, String textModelPrintName) throws Exception {
        evalForFilesAndClusterer("kmeans20", train, test, stringToVector, textModelPrintName);
        evalForFilesAndClusterer("kmeans100", train, test, stringToVector, textModelPrintName);
        evalForFilesAndClusterer("xmeans", train, test, stringToVector, textModelPrintName);
    }

    private static void evaluate(Filter wordModel, String wordModelPrintName) throws Exception {
        System.out.println("dataset1");
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"), wordModel, wordModelPrintName);
        System.out.println();
        System.out.println("dataset2");
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"), wordModel, wordModelPrintName);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("precision\trecall\tfmeasure");

        evaluate(ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR, "simple-text-model");
        System.out.println();
        System.out.println("=============================================================");
        System.out.println();
        evaluate(ClassifierTesterUtils.WIKI_STRING_TO_VECTOR, "wiki-text-model");
    }
}
