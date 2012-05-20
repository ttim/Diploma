package ru.abishev.weka.context;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class ContextClassifier extends Classifier {
    @Override
    public void buildClassifier(Instances data) throws Exception {
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
//        return super.classifyInstance(instance);    //To change body of overridden methods use File | Settings | File Templates.
        return 0;
    }
}
