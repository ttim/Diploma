package ru.abishev.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;

public class WekaUtils {
    public static Instances[] useSimpleWordsModel(Instances... instancesArray) throws Exception {
        StringToWordVector stringToWordVector = (StringToWordVector) readObjectFromFile(new File("./weka/string_to_word_vector"));
        stringToWordVector.setAttributeIndices((instancesArray[0].attribute("text").index() + 1) + "");
        stringToWordVector.setInputFormat(instancesArray[0]);

        for (Instances instances : instancesArray) {
            Filter.useFilter(instances, stringToWordVector);
        }

        Instances[] result = new Instances[instancesArray.length];
        for (int i = 0; i < instancesArray.length; i++) {
            result[i] = Filter.useFilter(instancesArray[i], stringToWordVector);
        }
        return result;
    }

    public static Instances removeCurrentUserInfo(Instances instances) throws Exception {
        Remove removeUserInfo = new Remove();
        removeUserInfo.setAttributeIndices((instances.attribute("_current_user").index() + 1) + "");
        removeUserInfo.setInputFormat(instances);
        return Filter.useFilter(instances, removeUserInfo);
    }

    public static void setupClass(Instances instances) {
        instances.setClass(instances.attribute("_result_category"));
    }

    public static void printEvalStat(Evaluation eval) throws Exception {
//        System.out.println("Summary string");
//        System.out.println(eval.toSummaryString());
//        System.out.println("Matrix string");
//        System.out.println(eval.toMatrixString());
        System.out.println("Class details string");
        System.out.println(eval.toClassDetailsString());
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
