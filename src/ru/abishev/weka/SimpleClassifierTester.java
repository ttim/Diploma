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

    public static void evalForClassifier(String classifierName, File train, File test, Filter stringToVector) throws Exception {
        System.out.println(classifierName);
        ClassifierTesterUtils.evalForClassifier(getSimpleClassifier(classifierName), train, test, stringToVector);
    }

    public static void evalForFiles(File train, File test, Filter stringToVector) throws Exception {
        evalForClassifier("naivebayes", train, test, stringToVector);
        evalForClassifier("svm", train, test, stringToVector);
        evalForClassifier("j48", train, test, stringToVector);
    }

    public static void main(String[] args) throws Exception {
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
    }
}
