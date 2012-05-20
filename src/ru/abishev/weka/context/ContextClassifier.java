package ru.abishev.weka.context;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextClassifier extends Classifier {
    private final Classifier classifier;
    private final Clusterer clusterer;
    private final Filter stringToVector;
    private final Map<String, ClustersContext> userToClusters = new HashMap<String, ClustersContext>();

    public ContextClassifier(Classifier classifier, Clusterer clusterer, Filter stringToVector) {
        // input to everything - with _user field
        this.classifier = classifier;
        this.clusterer = clusterer;
        this.stringToVector = stringToVector;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        classifier.buildClassifier(data);
    }

    @Override
    public double classifyInstance(Instance tweet) throws Exception {
        String user = tweet.stringValue(0);

        if (!userToClusters.containsKey(user)) {
            // prepare user tweets

            // should be as everything else

            Instances tweets = null;

            userToClusters.put(user, new ClustersContext(tweets, clusterer));
        }

        List<Instance> context = userToClusters.get(user).getContextInstances(tweet);

        // majority vote...
        Map<Double, Integer> resultToCount = new HashMap<Double, Integer>();
        for (Instance instance : context) {
            double classifyResult = classifier.classifyInstance(instance);
            if (resultToCount.containsKey(classifyResult)) {
                resultToCount.put(classifyResult, resultToCount.get(classifyResult) + 1);
            } else {
                resultToCount.put(classifyResult, 1);
            }
        }

        // find max
        double maxKey = resultToCount.keySet().iterator().next();
        for (double key : resultToCount.keySet()) {
            if (resultToCount.get(key) > resultToCount.get(maxKey)) {
                maxKey = key;
            }
        }
        return maxKey;
    }
}
