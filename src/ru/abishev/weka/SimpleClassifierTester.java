package ru.abishev.weka;

import ru.abishev.weka.api.ClassifierFactory;
import ru.abishev.weka.api.Dataset;
import ru.abishev.weka.api.WordModelFactory;
import ru.abishev.weka.impl.Classifiers;
import ru.abishev.weka.impl.Datasets;
import ru.abishev.weka.impl.WordModels;

public class SimpleClassifierTester {
    public static void evaluateForDataset(Dataset dataset) throws Exception {
        // config
        WordModelFactory[] wordModels = new WordModelFactory[]{
                WordModels.SIMPLE_WORD_MODEL,
                WordModels.WIKI_WORD_MODEL
        };

        ClassifierFactory[] classifiers = new ClassifierFactory[]{
                Classifiers.NAIVE_BAYES,
                Classifiers.SVM,
                Classifiers.J48
        };
        // end config

        System.out.println(dataset.getFullName());
        System.out.println("text-model\tclassifier\tprecision\trecall\tfmeasure");
        for (WordModelFactory wordModel : wordModels) {
            for (ClassifierFactory classifier : classifiers) {
                String printName = wordModel.getFullName() + "\t" + classifier.getFullName();
                ClassifierTesterUtils.evalForClassifier(printName, classifier.getClassifier(), dataset, wordModel.getWordModel());
            }
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Simple classifier");
        System.out.println();

        evaluateForDataset(Datasets.FIRST_TRAINTEST);
        evaluateForDataset(Datasets.SECOND_TRAINTEST);

        evaluateForDataset(Datasets.FIRST_CROSS);
        evaluateForDataset(Datasets.SECOND_CROSS);
    }
}
