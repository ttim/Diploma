package ru.abishev.weka;

import ru.abishev.weka.api.Dataset;
import ru.abishev.weka.wikitextmodel.LinkifierAlgo;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;

import java.io.File;
import java.util.Random;

import static ru.abishev.weka.WekaUtils.*;
import static ru.abishev.weka.WekaUtils.printEvalStat;

public class ClassifierTesterUtils {
    static {
        LinkifierAlgo.linkify("");
    }

    private static Instances[] readInstances(Filter wordModel, File... files) throws Exception {
        Instances[] result = new Instances[files.length];

        for (int i = 0; i < files.length; i++) {
            result[i] = read(files[i]);
        }

        for (Instances instances : result) {
            setupClass(instances);
        }

        result = useWordsModel(wordModel, result);

        return result;
    }

    public static void testClassifierCrossValidation(Classifier classifier, Dataset.CrossValidationDataset dataset, Filter wordModel) throws Exception {
        Instances instances = readInstances(wordModel, dataset.trainFile)[0];

        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifier, instances, dataset.foldsCount, new Random(1));

        printEvalStat(eval, instances);
    }

    public static void testClassifierWithTest(Classifier classifier, Dataset.TrainTestDataset dataset, Filter stringToVectorFilter) throws Exception {
        Instances[] result = readInstances(stringToVectorFilter, dataset.trainFile, dataset.testFile);
        Instances trainInstances = result[0], testInstances = result[1];

        Evaluation eval = new Evaluation(trainInstances);
        classifier.buildClassifier(trainInstances);
        eval.evaluateModel(classifier, testInstances);

        printEvalStat(eval, trainInstances);
    }


    public static void evalForClassifier(String classifierName, Classifier classifier, Dataset dataset, Filter wordModel) throws Exception {
        System.out.print(classifierName + "\t");
        if (dataset instanceof Dataset.CrossValidationDataset) {
            testClassifierCrossValidation(classifier, (Dataset.CrossValidationDataset) dataset, wordModel);
        } else {
            testClassifierWithTest(classifier, (Dataset.TrainTestDataset) dataset, wordModel);
        }
    }
}
