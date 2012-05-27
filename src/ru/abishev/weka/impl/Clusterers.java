package ru.abishev.weka.impl;

import ru.abishev.weka.WekaUtils;
import ru.abishev.weka.api.ClassifierFactory;
import ru.abishev.weka.api.ClustererFactory;
import weka.clusterers.Clusterer;

import java.io.File;

public class Clusterers {
    public static final ClustererFactory KMEANS_20 = getClustererFactoryFromFile("kmeans20");
    public static final ClustererFactory KMEANS_100 = getClustererFactoryFromFile("kmeans100");
    public static final ClustererFactory XMEANS = getClustererFactoryFromFile("xmeans");

    public static final ClustererFactory[] CLUSTERERS = new ClustererFactory[]{KMEANS_20, KMEANS_100, XMEANS};

    private static ClustererFactory getClustererFactoryFromFile(final String clustererName) {
        return new ClustererFactory() {
            @Override
            public Clusterer getClusterer() {
                return (Clusterer) WekaUtils.readObjectFromFile(new File("./weka/clusterers/" + clustererName));
            }

            @Override
            public String getFullName() {
                return clustererName;
            }
        };
    }
}
