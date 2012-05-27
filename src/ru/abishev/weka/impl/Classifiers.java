package ru.abishev.weka.impl;

import ru.abishev.weka.WekaUtils;
import ru.abishev.weka.api.ClassifierFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;

import java.io.File;

public class Classifiers {
    public static final ClassifierFactory NAIVE_BAYES = getClassifierFactoryFromFile("naivebayes");
    public static final ClassifierFactory SVM = getClassifierFactoryFromFile("svm");
    public static final ClassifierFactory J48 = getClassifierFactoryFromFile("j48");

    public static final ClassifierFactory[] CLASSIFIERS = new ClassifierFactory[]{NAIVE_BAYES, SVM, J48};

    private static ClassifierFactory getClassifierFactoryFromFile(final String classifierName) {
        return new ClassifierFactory() {
            @Override
            public Classifier getClassifier() {
                Classifier classifier = (Classifier) WekaUtils.readObjectFromFile(new File("./weka/classifiers/" + classifierName));

                FilteredClassifier filteredClassifier = new FilteredClassifier();
                filteredClassifier.setClassifier(classifier);
                filteredClassifier.setFilter(WekaUtils.getRemovingFilter());

                return filteredClassifier;
            }

            @Override
            public String getFullName() {
                return classifierName;
            }
        };
    }
}
