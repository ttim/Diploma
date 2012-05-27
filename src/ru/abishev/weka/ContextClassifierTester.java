package ru.abishev.weka;

import ru.abishev.weka.api.ClassifierFactory;
import ru.abishev.weka.api.ClustererFactory;
import ru.abishev.weka.api.Dataset;
import ru.abishev.weka.api.WordModelFactory;
import ru.abishev.weka.context.ContextClassifier;
import ru.abishev.weka.impl.Classifiers;
import ru.abishev.weka.impl.Clusterers;
import ru.abishev.weka.impl.Datasets;
import ru.abishev.weka.impl.WordModels;
import weka.classifiers.Classifier;
import weka.filters.Filter;

public class ContextClassifierTester {
    public static void evaluateForDataset(Dataset dataset) throws Exception {
        // config
        WordModelFactory[] wordModels = new WordModelFactory[]{
                WordModels.SIMPLE_WORD_MODEL,
//                WordModels.WIKI_WORD_MODEL
        };

        ClassifierFactory[] classifiers = new ClassifierFactory[]{
                Classifiers.NAIVE_BAYES,
                Classifiers.SVM,
                Classifiers.J48
        };

        ClustererFactory[] clusterers = new ClustererFactory[]{
                Clusterers.KMEANS_20,
                Clusterers.KMEANS_100,
                Clusterers.XMEANS
        };
        // end config

        System.out.println(dataset.getFullName());
        for (WordModelFactory wordModel : wordModels) {
            for (ClassifierFactory baseClassifier : classifiers) {
                for (ClustererFactory clusterer : clusterers) {
                    Filter createdWordModel = wordModel.getWordModel();
                    Classifier classifier = new ContextClassifier(baseClassifier, clusterer, wordModel.getFullName(), createdWordModel);
                    String printName = "context / " + wordModel.getFullName() + " / " + baseClassifier.getFullName() + "-" + clusterer.getFullName();
                    ClassifierTesterUtils.evalForClassifier(printName, classifier, dataset, createdWordModel);
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("precision\trecall\tfmeasure");
        System.out.println();

        evaluateForDataset(Datasets.FIRST_TRAINTEST);
        evaluateForDataset(Datasets.SECOND_TRAINTEST);

        evaluateForDataset(Datasets.FIRST_CROSS);
        evaluateForDataset(Datasets.SECOND_CROSS);
    }
}
