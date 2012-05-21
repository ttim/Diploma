package ru.abishev.weka;

import ru.abishev.weka.wikitextmodel.LinkifierAlgo;
import ru.abishev.weka.wikitextmodel.WikiTextModel;
import ru.abishev.wiki.linkifier.Linkifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.Random;

import static ru.abishev.weka.WekaUtils.*;
import static ru.abishev.weka.WekaUtils.printEvalStat;

public class ClassifierTesterUtils {
    static {
        LinkifierAlgo.linkify("");
    }

    public static final StringToWordVector SIMPLE_STRING_TO_VECTOR = (StringToWordVector) readObjectFromFile(new File("./weka/string_to_word_vector"));
    public static final Filter WIKI_STRING_TO_VECTOR = WikiTextModel.getWikiTextModel(3, "2");

    static {
        SIMPLE_STRING_TO_VECTOR.setAttributeIndices("2");
    }

    private static Instances[] readInstances(Filter stringToVectorFilter, File... files) throws Exception {
        Instances[] result = new Instances[files.length];

        for (int i = 0; i < files.length; i++) {
            result[i] = read(files[i]);
        }

        for (Instances instances : result) {
            setupClass(instances);
        }

        result = useWordsModel(stringToVectorFilter, result);

        return result;
    }

    public static void testClassifierCrossValidation(String classifierPrintName, Classifier classifier, File instancesFile, Filter stringToVectorFilter) throws Exception {
        Instances instances = readInstances(stringToVectorFilter, instancesFile)[0];
        System.out.println(classifierPrintName + " / " + "25% cross validation evaluation");
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifier, instances, 4, new Random(1));
        printEvalStat(eval, instances);
    }

    public static void testClassifierWithTest(String classifierPrintName, Classifier classifier, File instancesForTrain, File instancesForTest, Filter stringToVectorFilter) throws Exception {
        Instances[] result = readInstances(stringToVectorFilter, instancesForTrain, instancesForTest);
        Instances trainInstances = result[0], testInstances = result[1];

        System.out.println(classifierPrintName + " / " + "with test evaluation");
        Evaluation eval = new Evaluation(trainInstances);
        classifier.buildClassifier(trainInstances);
        eval.evaluateModel(classifier, testInstances);
        printEvalStat(eval, trainInstances);
    }


    public static void evalForClassifier(String classifierPrintName, Classifier classifier, File train, File test, Filter stringToVectorFilter) throws Exception {
        testClassifierWithTest(classifierPrintName, classifier, train, test, stringToVectorFilter);
        testClassifierCrossValidation(classifierPrintName, classifier, train, stringToVectorFilter);
        System.out.println();
    }
}
