package ru.abishev.weka;

import ru.abishev.weka.context.ContextClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.clusterers.Clusterer;
import weka.filters.Filter;

import java.io.File;

public class ContextClassifierTester {
    public static Classifier getContextClassifier(String baseClassifierName, String clustererName, Filter stringToVector) {
        Classifier baseClassifier = SimpleClassifierTester.getSimpleClassifier(baseClassifierName);
        Clusterer clusterer = (Clusterer) WekaUtils.readObjectFromFile(new File("./weka/clusterers/" + clustererName));

        // todo: add to clusterer ignoring of two first attributes!
        return new ContextClassifier(baseClassifier, clusterer, stringToVector);
    }

    public static void evalForClassifier(String baseClassifierName, String clustererName, File train, File test, Filter stringToVector) throws Exception {
        System.out.println(baseClassifierName + " / " + clustererName);
        ClassifierTesterUtils.evalForClassifier(getContextClassifier(baseClassifierName, clustererName, stringToVector), train, test, stringToVector);
    }

    public static void evalForFilesAndClusterer(String clustererName, File train, File test, Filter stringToVector) throws Exception {
        evalForClassifier("naivebayes", clustererName, train, test, stringToVector);
        evalForClassifier("svm", clustererName, train, test, stringToVector);
        evalForClassifier("j48", clustererName, train, test, stringToVector);
    }

    public static void evalForFiles(File train, File test, Filter stringToVector) throws Exception {
        evalForFilesAndClusterer("kmeans20", train, test, stringToVector);
    }

    public static void main(String[] args) throws Exception {
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"), ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
    }
}
