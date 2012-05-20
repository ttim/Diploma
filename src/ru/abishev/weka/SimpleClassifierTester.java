package ru.abishev.weka;

import ru.abishev.weka.ClassifierTesterUtils;

import java.io.*;

import static ru.abishev.weka.ClassifierTesterUtils.evalForClassifier;

public class SimpleClassifierTester {
    public static void evalForFiles(File train, File test) throws Exception {
        evalForClassifier("naivebayes", train, test, ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
        evalForClassifier("svm", train, test, ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
        evalForClassifier("j48", train, test, ClassifierTesterUtils.SIMPLE_STRING_TO_VECTOR);
    }

    public static void main(String[] args) throws Exception {
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"));
        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"));
    }
}
