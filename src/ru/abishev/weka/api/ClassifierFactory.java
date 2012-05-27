package ru.abishev.weka.api;

import weka.classifiers.Classifier;

public interface ClassifierFactory {
    public Classifier getClassifier();
    public String getFullName();
}
