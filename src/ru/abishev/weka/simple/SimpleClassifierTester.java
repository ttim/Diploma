package ru.abishev.weka.simple;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.io.*;
import java.util.Random;

import static ru.abishev.weka.WekaUtils.*;

public class SimpleClassifierTester {
    private static Instances[] readInstances(File... files) throws Exception {
        Instances[] result = new Instances[files.length];

        for (int i = 0; i < files.length; i++) {
            result[i] = read(files[i]);
            result[i] = removeCurrentUserInfo(result[i]);
        }

        result = useSimpleWordsModel(result);

        for (Instances instances : result) {
            setupClass(instances);
        }

        return result;
    }

    public static void testClassifierCrossValidation(Classifier classifier, File instancesFile) throws Exception {
        Instances instances = readInstances(instancesFile)[0];
        System.out.println("10% cross validation evaluation");
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifier, instances, 10, new Random(1));
        printEvalStat(eval);
    }

    public static void testClassifierWithTest(Classifier classifier, File instancesForTrain, File instancesForTest) throws Exception {
        Instances[] result = readInstances(instancesForTrain, instancesForTest);
        Instances trainInstances = result[0], testInstances = result[1];

        System.out.println("with test evaluation");
        Evaluation eval = new Evaluation(trainInstances);
        classifier.buildClassifier(trainInstances);
        eval.evaluateModel(classifier, testInstances);
        printEvalStat(eval);
    }


    public static void evalForFiles(File train, File test) throws Exception {
        System.out.println("Naive bayes");
        testClassifierCrossValidation(new NaiveBayes(), train);
        testClassifierWithTest(new NaiveBayes(), train, test);
        System.out.println("======================================");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        evalForFiles(new File("./train/thematic.train.arff"), new File("./train/thematic.test.arff"));
//        evalForFiles(new File("./train/usernewscompany.train.arff"), new File("./train/usernewscompany.test.arff"));
    }
}
