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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ClassifiersTester {
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

        ClustererFactory[] clusterers = new ClustererFactory[]{
                Clusterers.KMEANS_20,
                Clusterers.KMEANS_100,
                Clusterers.XMEANS
        };
        // end config

        System.out.println(dataset.getFullName());
        System.out.println("text-model\tclassifier\tclusterer\tprecision\trecall\tfmeasure");
        for (WordModelFactory wordModel : wordModels) {
            for (ClassifierFactory baseClassifier : classifiers) {
                {
                    // simple classifier
                    String printName = wordModel.getFullName() + "\t" + baseClassifier.getFullName() + "\t";
                    ClassifierTesterUtils.evalForClassifier(printName, baseClassifier.getClassifier(), dataset, wordModel.getWordModel());
                }

                for (ClustererFactory clusterer : clusterers) {
                    Filter createdWordModel = wordModel.getWordModel();
                    Classifier classifier = new ContextClassifier(baseClassifier, clusterer, wordModel.getFullName(), createdWordModel);
                    String printName = wordModel.getFullName() + "\t" + baseClassifier.getFullName() + "\t" + clusterer.getFullName();
                    ClassifierTesterUtils.evalForClassifier(printName, classifier, dataset, createdWordModel);
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errorStream);
        System.setErr(err);

        try {
            evaluateForDataset(Datasets.FIRST_TRAINTEST);
            evaluateForDataset(Datasets.SECOND_TRAINTEST);

            evaluateForDataset(Datasets.FIRST_CROSS);
            evaluateForDataset(Datasets.SECOND_CROSS);
        } finally {
            System.out.println();
            System.out.println("Error stream");
            System.out.println(errorStream.toString());
        }
    }
}
