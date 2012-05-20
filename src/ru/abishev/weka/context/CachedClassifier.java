package ru.abishev.weka.context;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class CachedClassifier extends Classifier {
    private final Classifier inner;
    private final Map<String, Double> cache = new HashMap<String, Double>();

    public CachedClassifier(Classifier inner) {
        this.inner = inner;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        cache.clear();
        inner.buildClassifier(data);
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
        String key = instance.toString();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        double result = inner.classifyInstance(instance);
        cache.put(key, result);
        return result;
    }
}
