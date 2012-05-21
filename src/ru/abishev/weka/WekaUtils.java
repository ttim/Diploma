package ru.abishev.weka;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;

public class WekaUtils {
    public static Filter getRemovingFilter() {
        Remove removeUserInfo = new Remove();
        removeUserInfo.setAttributeIndices("1");
        return removeUserInfo;
    }

    public static Instances[] useWordsModel(Filter stringToVectorTransform, Instances... instancesArray) throws Exception {
        stringToVectorTransform.setInputFormat(instancesArray[0]);

        for (Instances instances : instancesArray) {
            Filter.useFilter(instances, stringToVectorTransform);
        }

        Instances[] result = new Instances[instancesArray.length];
        for (int i = 0; i < instancesArray.length; i++) {
            result[i] = Filter.useFilter(instancesArray[i], stringToVectorTransform);
        }
        return result;
    }

    public static void setupClass(Instances instances) {
        instances.setClass(instances.attribute("_result_category"));
    }

    public static void printEvalStat(Evaluation eval, Instances data) throws Exception {
//        System.out.println("Summary string");
//        System.out.println(eval.toSummaryString());
//        System.out.println("Matrix string");
//        System.out.println(eval.toMatrixString());
//        System.out.println("Class details string");
//        System.out.println(eval.toClassDetailsString());

        // calc average precision
        int numClasses = data.numClasses();
        double precision = 0, recall = 0, fmeasure = 0;
        for (int i = 0; i < numClasses; i++) {
            precision += eval.precision(i);
            recall += eval.recall(i);
            fmeasure += eval.fMeasure(i);
        }
        precision /= numClasses;
        recall /= numClasses;
        fmeasure /= numClasses;

        System.out.printf("%.3f\t%.3f\t%.3f%n", precision, recall, fmeasure);

    }

    public static Instances read(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Instances data = new Instances(reader);
            reader.close();

            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object readObjectFromFile(File file) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            Object object = input.readObject();
            input.close();
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
