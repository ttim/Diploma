package ru.abishev.weka.api;

import weka.clusterers.Clusterer;

public interface ClustererFactory {
    public Clusterer getClusterer();

    public String getFullName();
}
